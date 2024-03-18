import 'package:cim_flutter_websocket_sdk/socket_io_connect.dart';
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
    cimSocket!.init('wss.hoxin.farsunset.com', 443, '16501516154949', true);
    cimSocket!.connect(
        devicename: "Windows 10 Pro",
        appVersion: "1.0.0",
        osVersion: "10.0.19042",
        packageName: "com.farsunset.cim",
        deviceid: "16501516154949",
        language: "zh-CN",
        channelName: "web");
  }

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
}
