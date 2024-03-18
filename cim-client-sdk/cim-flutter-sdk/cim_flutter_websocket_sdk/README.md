# cim_flutter_websocket_sdk

开源通讯库 [CIM](https://gitee.com/farsunset/cim) 的 flutter版本 SDK 由于使用了 websocket 代替 Tcp长连接,额外支持了Web浏览器

## 长连接版本 

[在这里](https://pub.dev/packages/cim_flutter_sdk)


## 如何在自己的项目中引用SDK

在 pubspec.yaml 引入


```
dependencies:
  cim_flutter_websocket_sdk: ^1.0.1
```


## 如何使用

```
import 'package:cim_flutter_sdk/cim_socket.dart';
...

  late CIMSocket? cimSocket = null;

  late List<String> list = [];

    late String connectStatus = 'Disconnected';

  @override
  void initState() {
    super.initState();
    cimSocket = CIMSocket(onMessageReceived: (value) {
      setState(() {
        list.add(value.toProto3Json().toString());
      });
    }, onConnectionStatusChanged: (value) {
      setState(() {
        connectStatus = value;
      });
    });
    cimSocket!.init('127.0.0.1', 45678, '16501516154949', true);
     cimSocket!.connect(
        devicename: "Windows 10 Pro",
        appVersion: "1.0.0",
        osVersion: "10.0.19042",
        packageName: "com.farsunset.cim",
        deviceid: "16501516154949",
        language: "zh-CN",
        channelName: "web");
  }
...

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text(connectStatus),
        ),
        body: ListView.builder(
          itemCount: list.length,
          itemBuilder: (context, index) {
            return Text(list[index]);
          },
        ),
      ),
    );
  }

```