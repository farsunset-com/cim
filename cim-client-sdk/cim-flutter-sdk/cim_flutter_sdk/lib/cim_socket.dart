import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:common_utils/common_utils.dart';
import 'package:crypto/crypto.dart';
import 'package:device_info_plus/device_info_plus.dart';
import 'package:flutter/material.dart';

import './Message.pb.dart' as messages;
import './ReplyBody.pb.dart' as replybody;
import './SentBody.pb.dart' as sentbody;
import 'package:fixnum/fixnum.dart';
import 'package:convert/convert.dart';

//PONG
// ignore: constant_identifier_names
const PONG_TYPE = 0;
//发送消息类型
// ignore: constant_identifier_names
const Message_TYPE = 2;
//强制下线类型
// ignore: constant_identifier_names
const ACTION_999 = 999;
//响应消息类型
// ignore: constant_identifier_names
const REPLY_BODY = 4;
//消息发送
// ignore: constant_identifier_names
const SEND_BODY = 3;
//PING
// ignore: constant_identifier_names
const PING_TYPE = 1;

// ignore: constant_identifier_names
const DATA_HEADER_LENGTH = 1;

const sOCKETAPPVERSION = '100';

class CIMSocket {
  CIMSocket(
      {required this.onMessageReceived,
      required this.onConnectionStatusChanged});

  late Socket? socket;
  late String? uri;
  late int? port;
  late String? uid;
  late String endCode = "0";
  final DeviceInfoPlugin deviceInfoPlugin = DeviceInfoPlugin();
  final ValueChanged<messages.Model> onMessageReceived;
  final ValueChanged<bool> onConnectionStatusChanged;
  late TimerUtil timer = TimerUtil()
    ..mTotalTime = 10000
    ..setOnTimerTickCallback((millisUntilFinished) {
      if (millisUntilFinished == 0 && uid != null) {
        connect();
      }
    });

  late String? devicename;

  late String? appVersion;

  late String? osVersion;

  late String? packageName;

  late String? deviceid;

  late String? language;

  late String? channelName;

  Future init(String uri, int port, String uid) async {
    this.uri = uri;
    this.port = port;
    this.uid = uid;
  }

  //登录
  Future connect(
      {String? devicename,
      String? appVersion,
      String? osVersion,
      String? packageName,
      String? deviceid,
      String? language,
      String? channelName}) async {
    this.devicename = devicename;
    this.appVersion = appVersion;
    this.osVersion = osVersion;
    this.packageName = packageName;
    this.deviceid = deviceid;
    this.language = language;
    this.channelName = channelName;

    if (uri == null || port == null || uid == null) {
      throw IOException;
    }
    Socket.connect(uri, port!).then((Socket sock) {
      sock.listen((data) async {
        int l = (data[1] & 0xff);
        int h = (data[2] & 0xff);
        int length = (l | h << 8);
        if (data[0] == PING_TYPE) {
          sendPong();
        } else if (data[0] == REPLY_BODY) {
          var message = data.sublist(3, length + 3);
          replybody.Model info = replybody.Model();
          info.mergeFromBuffer(message);
        } else if (data[0] == Message_TYPE) {
          var message = data.sublist(3, length + 3);
          messages.Model model = messages.Model();
          model.mergeFromBuffer(message);
          onMessageReceived(model);
        }
      }, onError: (error, StackTrace trace) {
        socket = null;
        if (!timer.isActive()) {
          timer.setTotalTime(12000);
          timer.startCountDown();
        }
        onConnectionStatusChanged(false);
      }, onDone: () {
        socket = null;
        if (endCode != "999") {
          timer.setTotalTime(12000);
          timer.startCountDown();
        }
        onConnectionStatusChanged(false);
      }, cancelOnError: true);
      socket = sock;
      sendLoginMsg();
    }).catchError((e) {
      socket = null;
      timer.setTotalTime(12000);
      timer.startCountDown();
      onConnectionStatusChanged(false);
    });
  }

  //登出
  Future disConnect() async {
    if (socket != null) {
      endCode = '999';
      await socket!.close();
    }
  }

  //发送登录消息
  Future sendLoginMsg() async {
    SystemInfo systemInfo = SystemInfo();
    await systemInfo.init();
    String deviceName = devicename ?? systemInfo.deviceName;
    // DeviceInfoPlugin deviceInfoPlugin = DeviceInfoPlugin();
    String channel = channelName ?? systemInfo.deviceName;
    String systemVersion = osVersion ?? systemInfo.version;
    String deviceId = deviceid ??
        hex.encode(md5
            .convert(const Utf8Encoder().convert(systemInfo.deviceId))
            .bytes);

    Map<String, String> map = {
      "uid": uid!, //主id
      "channel": channel,
      "appVersion": appVersion ?? sOCKETAPPVERSION,
      "osVersion": systemVersion,
      "packageName": packageName ?? "cn.asihe.cim",
      "deviceId": deviceId,
      // (await PlatformDeviceId.getDeviceId)!.replaceAll("-", ""), //应用id
      "deviceName": '$deviceName ${systemInfo.model}',
      "language": language ?? "zh-CN",
    };
    int time = DateTime.now().millisecondsSinceEpoch;
    Int64 timeStamp = Int64.parseInt(time.toString());
    var body = sentbody.Model(data: map);
    body.key = "client_bind";
    body.timestamp = timeStamp;
    var data = body.writeToBuffer();
    var protobuf = Uint8List(data.length + 3);
    protobuf[0] = 3;
    protobuf[1] = (data.length & 0xff);
    protobuf[2] = ((data.length >> 8) & 0xff);
    protobuf.setRange(3, data.length + 3, data);
    socket!.add(protobuf);
    await socket!.flush();
    onConnectionStatusChanged(true);
    if (timer.isActive()) {
      timer.cancel();
    }
  }

//发送PONG响应
  Future sendPong() async {
    var pONG = Uint8List(7);
    var pONGBODY = Uint8List(4);
    pONGBODY[0] = 80;
    pONGBODY[1] = 79;
    pONGBODY[2] = 78;
    pONGBODY[3] = 71;
    pONG[0] = PONG_TYPE;
    pONG[1] = (pONGBODY.length & 0xff);
    pONG[2] = ((pONGBODY.length >> 8) & 0xff);
    pONG.setRange(3, 6, pONGBODY);
    socket!.add(pONG);
    await socket!.flush();
    if (timer.isActive()) {
      timer.cancel();
    }
  }
}

class SystemInfo {
  String deviceName = 'cim_entity';
  String version = '0.0.1';
  String deviceId = 'CIM Entity';
  String model = "10";
  final DeviceInfoPlugin deviceInfoPlugin = DeviceInfoPlugin();

  Future init() async {
    if (Platform.isAndroid) {
      // Android相关代码
      var value = await deviceInfoPlugin.androidInfo;
      deviceName = 'android';
      version = Platform.version.substring(0, 6);
      deviceId = value.id;
      model = value.model;
    } else if (Platform.isIOS) {
      // iOS相关代码
      var value = await deviceInfoPlugin.iosInfo;
      deviceName = 'ios';
      version = Platform.version.substring(0, 6);
      deviceId = value.identifierForVendor!;
      model = value.model!;
    } else if (Platform.isMacOS) {
      // MacOS相关代码
      var value = await deviceInfoPlugin.macOsInfo;

      deviceName = 'mac';
      version = Platform.version.substring(0, 6);
      deviceId = value.model;
      model = value.model;
    } else if (Platform.isWindows) {
      // Windows相关代码
      var value = await deviceInfoPlugin.windowsInfo;
      deviceName = 'windows';
      version = Platform.version.substring(0, 6);
      deviceId = value.deviceId;
      model = value.majorVersion.toString();
    } else if (Platform.isLinux) {
      // Linux相关代码
      var value = await deviceInfoPlugin.linuxInfo;
      deviceName = 'linux';
      version = Platform.version.substring(0, 6);
      deviceId = value.id;
      model = value.version!;
    }
  }
}
