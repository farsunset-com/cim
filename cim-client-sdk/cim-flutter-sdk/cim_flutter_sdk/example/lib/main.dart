import 'package:cim_flutter_sdk/cim_socket.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
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
    cimSocket!.init('api.hoxin.farsunset.com', 34567, '16501516154949');
    cimSocket!.connect(
        devicename: "Windows 10 Pro",
        appVersion: "2.1.0",
        osVersion: "10.0.19042.1165",
        packageName: "com.farsunset.flutter",
        deviceid: "asd52d1d0a6s1f6sdf1",
        language: "zh_CN");
  }

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
}
