package proto

import (
	"context"
	"log"
	"sync"
	"time"

	"github.com/gorilla/websocket"
	pb "google.golang.org/protobuf/proto"
)

// 消息类型（第 1 个字节）
const (
	PONG    = 0
	PING    = 1
	MESSAGE = 2
	SENT    = 3
	REPLY   = 4
)

// pong
var pongBody = []byte{80, 79, 78, 71} // "PONG"

type CIMClientService struct {
	// websocket 连接
	ws *websocket.Conn

	// 基础信息
	url        string
	token      string
	currentUID string

	// 生命周期控制
	ctx    context.Context
	cancel context.CancelFunc

	// 写队列（所有写操作必须串行）
	sendChan chan []byte

	// 重连控制
	reconnectAttempts int
	maxReconnectDelay time.Duration

	// 回调（贴近 JS WebSocket 使用方式）
	onConnect       func()
	onMessage       func(map[string]interface{})
	onReplyReceived func(map[string]interface{})
	onClose         func()

	mu sync.Mutex
}

// -------------------- 单例 --------------------

var socketInstance *CIMClientService
var socketOnce sync.Once

func GetCIMClientService() *CIMClientService {
	socketOnce.Do(func() {
		socketInstance = &CIMClientService{
			maxReconnectDelay: 30 * time.Second,
			sendChan:          make(chan []byte, 100),
		}
	})
	return socketInstance
}

// Connect 登录后调用
func (c *CIMClientService) Connect(token, uid string) {
	c.mu.Lock()
	defer c.mu.Unlock()

	// 如果已经是同一个用户的连接，直接忽略
	if c.ws != nil && c.currentUID == uid {
		return
	}

	// 关闭旧连接
	if c.cancel != nil {
		c.cancel()
	}

	c.token = token
	c.currentUID = uid
	c.url = Config.CIMHost

	// 创建新的 context
	c.ctx, c.cancel = context.WithCancel(context.Background())

	go c.run()
}

// Disconnect 用户主动断开
func (c *CIMClientService) Disconnect() {
	c.mu.Lock()
	defer c.mu.Unlock()

	if c.cancel != nil {
		c.cancel()
	}
	c.cleanup()
}

// run 是整个 socket 的“总控循环”
// 保证：永远只有一个 run 在执行
func (c *CIMClientService) run() {
	for {
		select {
		case <-c.ctx.Done():
			return
		default:
		}

		// 尝试建立连接
		if err := c.connectOnce(); err != nil {
			c.sleepBeforeReconnect()
			continue
		}

		// 连接成功
		if c.onConnect != nil {
			c.onConnect()
		}

		// 绑定账号（只在连接成功后做）
		if err := c.BindAccount(c.currentUID); err != nil {
			log.Println("BindAccount error:", err)
		}

		// 启动写协程
		go c.writeLoop()

		// 阻塞读取（直到断线）
		c.readLoop()

		// 读循环退出，说明连接断了
		if c.onClose != nil {
			c.onClose()
		}

		c.cleanup()
		c.sleepBeforeReconnect()
	}
}

// 单次连接
func (c *CIMClientService) connectOnce() error {
	dialer := websocket.DefaultDialer
	conn, _, err := dialer.Dial(c.url, nil)
	if err != nil {
		log.Println("WebSocket dial error:", err)
		return err
	}

	c.mu.Lock()
	c.ws = conn
	c.reconnectAttempts = 0
	c.mu.Unlock()

	return nil
}

// 断线后的重连等待（指数退避）
func (c *CIMClientService) sleepBeforeReconnect() {
	c.reconnectAttempts++
	delay := time.Duration(float64(time.Second) * pow(1.5, float64(c.reconnectAttempts)))
	if delay > c.maxReconnectDelay {
		delay = c.maxReconnectDelay
	}

	log.Printf("WebSocket %v 后重连\n", delay)

	select {
	case <-time.After(delay):
	case <-c.ctx.Done():
	}
}

/*
=====================
 读 / 写循环
=====================
*/

// readLoop 负责读取服务器消息
func (c *CIMClientService) readLoop() {
	for {
		select {
		case <-c.ctx.Done():
			return
		default:
		}

		_, msg, err := c.ws.ReadMessage()
		if err != nil {
			log.Println("WebSocket read error:", err)
			return
		}
		c.handleMessage(msg)
	}
}

// writeLoop 负责所有写操作（串行，gorilla/websocket 要求）
func (c *CIMClientService) writeLoop() {
	for {
		select {
		case data := <-c.sendChan:
			if c.ws != nil {
				_ = c.ws.WriteMessage(websocket.BinaryMessage, data)
			}
		case <-c.ctx.Done():
			return
		}
	}
}

/*
=====================
 消息处理
=====================
*/

func (c *CIMClientService) handleMessage(msg []byte) {
	if len(msg) == 0 {
		return
	}

	msgType := msg[0]
	body := msg[1:]

	switch msgType {
	case PING:
		c.sendPong()

	case MESSAGE:
		m, err := c.getMessage(body)
		if err != nil {
			log.Println("decode message error:", err)
			return
		}
		if c.onMessage != nil {
			c.onMessage(m)
		}

	case REPLY:
		r, err := c.getReply(body)
		if err != nil {
			log.Println("decode reply error:", err)
			return
		}
		if c.onReplyReceived != nil {
			c.onReplyReceived(r)
		}

	default:
		log.Printf("未知消息类型: %d, len=%d\n", msgType, len(body))
	}
}

/*
=====================
 对外发送方法
=====================
*/

// BindAccount 绑定账号（连接成功后调用）
func (c *CIMClientService) BindAccount(uid string) error {
	var body proto.SentBodyModel
	body.Key = "client_bind"
	body.Timestamp = time.Now().Unix()
	body.Data = map[string]string{
		"uid":        uid,
		"channel":    "go-client",
		"appVersion": "1.0.0",
		"osVersion":  "16.0.0",
		"deviceId":   "go-device-001",
		"deviceName": "Go Client",
		"language":   "zh-CN",
	}
	return c.sendProto(SENT, &body)
}

// 发送 protobuf 消息（统一出口）
func (c *CIMClientService) sendProto(msgType byte, body pb.Message) error {
	data, err := pb.Marshal(body)
	if err != nil {
		return err
	}

	buf := make([]byte, len(data)+1)
	buf[0] = msgType
	copy(buf[1:], data)

	select {
	case c.sendChan <- buf:
	default:
		log.Println("send buffer full, drop message")
	}
	return nil
}

// 发送 pong
func (c *CIMClientService) sendPong() {
	buf := make([]byte, len(pongBody)+1)
	buf[0] = PONG
	copy(buf[1:], pongBody)

	select {
	case c.sendChan <- buf:
	default:
	}
}

/*
=====================
 protobuf 解码
=====================
*/

func (c *CIMClientService) getMessage(data []byte) (map[string]interface{}, error) {
	var body proto.MessageModel
	if err := pb.Unmarshal(data, &body); err != nil {
		return nil, err
	}

	return map[string]interface{}{
		"id":        body.GetId(),
		"action":    body.GetAction(),
		"content":   body.GetContent(),
		"sender":    body.GetSender(),
		"receiver":  body.GetReceiver(),
		"extra":     body.GetExtra(),
		"title":     body.GetTitle(),
		"format":    body.GetFormat(),
		"timestamp": body.GetTimestamp(),
	}, nil
}

func (c *CIMClientService) getReply(data []byte) (map[string]interface{}, error) {
	var body proto.ReplyBodyModel
	if err := pb.Unmarshal(data, &body); err != nil {
		return nil, err
	}

	return map[string]interface{}{
		"code":      body.GetCode(),
		"key":       body.GetKey(),
		"message":   body.GetMessage(),
		"timestamp": body.GetTimestamp(),
		"data":      body.GetData(),
	}, nil
}

/*
=====================
 回调注册
=====================
*/

func (c *CIMClientService) OnConnect(fn func()) {
	c.onConnect = fn
}

func (c *CIMClientService) OnMessage(fn func(map[string]interface{})) {
	c.onMessage = fn
}

func (c *CIMClientService) OnReplyReceived(fn func(map[string]interface{})) {
	c.onReplyReceived = fn
}

func (c *CIMClientService) OnClose(fn func()) {
	c.onClose = fn
}

/*
=====================
 工具方法
=====================
*/

func (c *CIMClientService) cleanup() {
	c.mu.Lock()
	defer c.mu.Unlock()

	if c.ws != nil {
		_ = c.ws.Close()
		c.ws = nil
	}
}

func pow(a, b float64) float64 {
	res := 1.0
	for i := 0; i < int(b); i++ {
		res *= a
	}
	return res
}
