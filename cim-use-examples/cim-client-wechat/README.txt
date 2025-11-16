#微信小程序客户端代码示例
```
createWebsocketConnection()中设置当前账号和终端ID
body.data.uid = '10000';//设置你的用户ID
```

该示例使用json编解码策略，没有使用protobuf，所以服务端需要设置消息编码格式为json

如果使用protobuf 请使用如下SDK
cim-client-sdk/cim-uniapp-sdk