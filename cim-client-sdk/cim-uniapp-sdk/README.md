**文档最新更新时间：****2023年10月25日**

## 1、常用功能接口
所有开放外部接口都集中在uni.socket.js
服务端配置地址配置以及客户端相关配置都在这里面

### 1.0连接服务器
初始化完成后，调用连接服务器
```javascript
const uniSocket =  new UniSocket({
	url: 'websocketUrl'
});
```
### 1.1绑定账号
当socket连接成功回调，然后绑定用户ID
```javascript
uniSocket.on('connectioned', function() {
  uniSocket.bindAccount(id)
}, true)
```
### 1.2绑定会议房间号
当socket连接成功回调，绑定会议房间号Tag
```javascript
uniSocket.bindRoomTag(tag) // 会议结束删除房间号传null或者空
```
### 1.3接收消息
```javascript
uniSocket.on('*', async (message) => {
	
})
```

### 1.4停止接收消息
停止接受推送，将会退出当前账号登录，端口与服务端的连接
```javascript
uniSocket.close();
```

### 1.5恢复接收消息
重新恢复接收推送，重新连接服务端，并登录当前账号
```javascript
uniSocket.reconnection();
```

### 1.6发送SentBody请求
支持通过长连接发送一个异步请求到服务的进行处理
例如发送一个位置上报请求
key ：client_cycle_location 需要在服务端创建一个实现的handler参照BindHandler

#### 1.6.1 protobuf序列化
```javascript
const SENT_BODY = 3
var body = new proto.com.farsunset.cim.sdk.web.model.SentBody();
body.setKey("client_cycle_location");
body.getDataMap().set("uid","10000");
body.getDataMap().set("latitude","123.82455");
body.getDataMap().set("longitude","412.245645");
body.getDataMap().set("location","上海市徐汇区云景路8弄");
uniSocket.sendRequest(body)
```

#### 1.6.2 json序列化
```javascript
let body = {};
body.key ="client_cycle_location";
body.timestamp=new Date().getTime();
body.data = {};
body.data.uid = 10000;
body.data.latitude = 123.82455;
body.data.longitude = 412.245645;
body.data.location = "上海市徐汇区云景路8弄";
uniSocket.sendRequest(body)
```