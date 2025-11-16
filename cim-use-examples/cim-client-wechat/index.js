
const MESSAGE = 2;
const REPLY_BODY = 4;
const SENT_BODY = 3;
const PING = 1;
const PONG = 0;

let self;

  Page({
    /**
     * 页面的初始数据
     */
    data: {
        websocketTask:undefined,
        websocketConnected:false,
        websocketConnectedTimes:0,
        wssUri:"wss://your.doman.com",
       
    },
 
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      self = this;
      this.createWebsocketConnection();
    },

    
    onShow() {
      
    },

    sendWebsocketPong(){
      if(this.data.websocketTask == undefined){
        return;
      }

      let pong = {};
      pong.type = PONG; 
      pong.content = 'PONG';

      this.data.websocketTask.send({data:JSON.stringify(pong)});
    },

    createWebsocketConnection(){
    

      let websocketTask = wx.connectSocket({url: this.data.wssUri,});

      websocketTask.onOpen(function(res) {

        self.setData({websocketTask:websocketTask});

         /** 链接成功发送bind请求*/
        let body = {};
        body.key = "client_bind";
        body.data = {};
        body.data.uid = '10000';//设置你的用户ID
        body.data.channel = 'wechat';
        body.data.deviceId = getDeivceId();
        body.data.appVersion = '1.0.0';
        body.data.osVersion = wx.getSystemInfoSync().version;
        body.data.deviceName = wx.getSystemInfoSync().platform;


        let data = {};
        data.type = SENT_BODY;
        data.content = JSON.stringify(body);
        wx.sendSocketMessage({data:JSON.stringify(data)});
      });

      websocketTask.onMessage(function(res) {
         let message = JSON.parse(res.data);
         let type = message.type;
         if (type === PING) {
          self.sendWebsocketPong();
          return;
         }

         if (type === REPLY_BODY) {
          //bind成功
          self.onWebsocketConnected();
          return;
         }

         //拿到了后端发送的消息JSON
         let content = JSON.parse(message.content);

         
      });
      

      websocketTask.onClose(function(res) {
        self.setData({websocketConnected:false});
        if(res.reason != 'FINISH'){
          // 在5秒后执行一次
          setTimeout(function(){
            self.createWebsocketConnection(self.data.room.id);
          }, 5000);
        }
     });
    },

    onWebsocketConnected(){
      
    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {
      let websocketTask = this.data.websocketTask;
      if(websocketTask != null && websocketTask != undefined){
        websocketTask.close({code:1000,reason:"FINISH"});
      }
    },

    getDeivceId() {
      let deviceId = wx.getStorageSync('x-device-id');
      if(deviceId != undefined && deviceId != ''){
        return deviceId;
      }

      let d = new Date().getTime();
      let uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
          let r = (d + Math.random() * 16) % 16 | 0;
          d = Math.floor(d / 16);
          return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
      });

      let newDeviceId = uuid.replace(/-/g, '');
      wx.setStorageSync('x-device-id', newDeviceId);
      return newDeviceId;
  }
   
  })