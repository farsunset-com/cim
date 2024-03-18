import 'dart:convert';
import 'dart:io';
import 'package:crypto/crypto.dart';
import 'package:device_info_plus/device_info_plus.dart';
import 'package:flutter/foundation.dart';

import './Message.pb.dart' as messages;
import './ReplyBody.pb.dart' as replybody;
import './SentBody.pb.dart' as sentbody;
import 'package:fixnum/fixnum.dart';
import 'package:convert/convert.dart';

// ignore_for_file: avoid_print
import 'package:web_socket_client/web_socket_client.dart';

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

  /// 句柄
  late WebSocket? channel;

  /// 地址
  late String? uri;

  /// 端口
  late int? port;

  /// 用户id
  late String? uid;

  ///
  late String endCode = "0";

  /// 是否使用SSL
  late bool useSsl = false;

  /// 设备信息
  final DeviceInfoPlugin deviceInfoPlugin = DeviceInfoPlugin();

  /// 消息回调
  final ValueChanged<messages.Model> onMessageReceived;

  /// 连接状态回调
  final ValueChanged<String> onConnectionStatusChanged;

  /// 设备名
  late String? devicename;

  /// app版本
  late String? appVersion;

  /// 系统版本
  late String? osVersion;

  /// 包名
  late String? packageName;

  /// 设备id
  late String? deviceid;

  /// 语言
  late String? language;

  ///
  late String? channelName;

  ///
  /// [uri] 地址
  /// [port] 端口
  /// [uid] 用户id
  /// [useSsl] 是否使用ssl
  Future init(String uri, int port, String uid, bool useSsl) async {
    this.uri = uri;
    this.port = port;
    this.uid = uid;
    this.useSsl = useSsl;
  }

  /// 连接
  /// [devicename] 设备名
  /// [appVersion] app版本
  /// [osVersion] 系统版本
  /// [packageName] 包名
  /// [deviceid] 设备id
  /// [language] 语言
  /// [channelName] 渠道名
  Future connect({
    String? devicename,
    String? appVersion,
    String? osVersion,
    String? packageName,
    String? deviceid,
    String? language,
    String? channelName,
  }) async {
    if (uri == null || port == null || uid == null) {
      throw IOException;
    }

    this.devicename = devicename;
    this.appVersion = appVersion;
    this.osVersion = osVersion;
    this.packageName = packageName;
    this.deviceid = deviceid;
    this.language = language;
    this.channelName = channelName;

    channel = WebSocket(Uri.parse('${useSsl ? 'wss' : 'ws'}://$uri:$port'),
        backoff: ConstantBackoff(Duration(seconds: 10)),
        binaryType: 'arraybuffer');

    channel!.connection.listen((event) {
      if (event.runtimeType == Connected || event.runtimeType == Reconnected) {
        sendLoginMsg();
        onConnectionStatusChanged(
            event.runtimeType == Connected ? 'Connected' : 'Reconnected');
      } else {
        onConnectionStatusChanged(event.runtimeType.toString());
      }
    });

    channel!.messages.listen((message) {
      Uint8List data = message;
      if (data[0] == PING_TYPE) {
        sendPong();
      } else if (data[0] == REPLY_BODY) {
        var message = data.sublist(1, data.length);
        replybody.Model info = replybody.Model();
        info.mergeFromBuffer(message);
      } else if (data[0] == Message_TYPE) {
        var message = data.sublist(1, data.length);
        messages.Model model = messages.Model();
        model.mergeFromBuffer(message);
        onMessageReceived(model);
      }
    }, onError: (error, StackTrace trace) {
      channel = null;
      onConnectionStatusChanged('Disconnected');
    }, onDone: () {
      channel = null;
      onConnectionStatusChanged('Disconnected');
    }, cancelOnError: true);
  }

  //登出
  Future disConnect() async {
    if (channel != null) {
      endCode = '999';
      channel!.close();
    }
  }

  //发送登录消息
  Future sendLoginMsg() async {
    SystemInfo systemInfo = SystemInfo();
    await systemInfo.init();
    String deviceName = devicename ?? systemInfo.deviceName;
    // DeviceInfoPlugin deviceInfoPlugin = DeviceInfoPlugin();
    String channelN = channelName ?? systemInfo.deviceName;
    String systemVersion = osVersion ?? systemInfo.version;
    String deviceId = deviceid ??
        hex.encode(md5
            .convert(const Utf8Encoder().convert(systemInfo.deviceId))
            .bytes);

    Map<String, String> map = {
      "uid": uid!, //主id
      "channel": channelN,
      "appVersion": sOCKETAPPVERSION,
      "osVersion": systemVersion,
      "packageName": packageName ?? "cn.asihe.cim",
      "deviceId": deviceId,
      // (await PlatformDeviceId.getDeviceId)!.replaceAll("-", ""), //应用id
      "deviceName": '$deviceName',
      "language": language ?? "zh-CN",
    };
    int time = DateTime.now().millisecondsSinceEpoch;
    Int64 timeStamp = Int64.parseInt(time.toString());
    var body = sentbody.Model(data: map);
    body.key = "client_bind";
    body.timestamp = timeStamp;
    var data = body.writeToBuffer();
    var protobuf = Uint8List(data.length + 1);
    protobuf[0] = 3;
    protobuf.setRange(1, data.length + 1, data);
    channel!.send(protobuf);
  }

//发送PONG响应
  Future sendPong() async {
    var pONGBODY = Uint8List(4);
    var pONG = Uint8List(pONGBODY.length + 1);
    pONGBODY[0] = 80;
    pONGBODY[1] = 79;
    pONGBODY[2] = 78;
    pONGBODY[3] = 71;
    pONG[0] = 0;
    pONG.setRange(1, 4, pONGBODY);
    channel!.send(pONG);
  }
}

class SystemInfo {
  String deviceName = 'cim_entity';
  String version = '0.0.1';
  String deviceId = 'CIM Entity';
  String model = "10";
  final DeviceInfoPlugin deviceInfoPlugin = DeviceInfoPlugin();

  Future init() async {
    if (kIsWeb) {
      var value = await deviceInfoPlugin.webBrowserInfo;
      deviceName = "web";
      version = value.appVersion!.substring(0, 6);
      deviceId = value.appCodeName!;
      model = value.browserName.name;
      return;
    }

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
