# cim_flutter_sdk

开源通讯库 [CIM](https://gitee.com/farsunset/cim) 的 flutter版本SDK


## WebSocket版本 

[在这里](https://pub.dev/packages/cim_flutter_websocket_sdk)

## 如何在自己的项目中引用SDK

在 pubspec.yaml 引入


```
dependencies:
  cim_flutter_sdk: ^1.0.6
```

  
## 如何使用

```
import 'package:cim_flutter_sdk/cim_socket.dart';
...

  late CIMSocket? cimSocket = null;

  late List<String> list = [];

  late bool connectStatus = false;

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
    cimSocket!.init('127.0.0.1', 34567, '16501516154949');
    cimSocket!.connect(
        devicename: "Windows 10 Pro",
        appVersion: "2.1.0",
        osVersion: "10.0.19042.1165",
        packageName: "com.farsunset.flutter",
        deviceid: "asd52d1d0a6s1f6sdf1",
        language: "zh_CN");
  }
...

 @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text(connectStatus ? '已连接' : '未连接'),
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
