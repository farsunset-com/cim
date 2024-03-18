/*
 * @Author: UpYou
 * @Date: 2020-12-25 9:14:50
 * @LastEditTime: 2020-12-29 16:09:04
 * @Description: uni.socket plug-in is developed based on uniapp...
 */
import "./message.js";
import "./replybody.js";
import "./sentbody.js";
let APP_VERSION = "1.0.0";
let APP_CHANNEL = 'app'
const APP_PACKAGE = "com.farsunset.cim";
const MESSAGE = 2;
const REPLY_BODY = 4;
const SENT_BODY = 3;
const PING = 1;
const PONG = 0;
const DATA_HEADER_LENGTH = 1;
const PONG_BODY = new Uint8Array([80, 79, 78, 71]);
export default class Socket {
	constructor(option = {}) {
		this._url = option.url;
		// 是否设置重新连接
		this._reconnection = option.reconnection || true;
		// 是否建立缓存池,默认true，如果建立缓存池，会将因为程序错误导致未发送成功的消息发送
		this._buffer = option.buffer || true;
		/// on方法注册的事件
		this.on_register = {};
		// 是否已成功连接
		this._connectioned = false;
		// 缓存池
		this._buffer_register = [];
		// 发送缓存池的数据
		this._auto_emit_buffer_data = option.autoEmitBuffer || false;
		// 被动断开
		this.closed = false;
		// 开始重连
		this.begin_reconnection = false;
		// 多少毫秒发送一次心跳
		this._heart_rate = option.heartRate > 0 ? option.heartRate : 60000;
		// 后端心跳字段
		this._heart_rate_type = option.heartRateType || "HEARTBEAT";
		this.init();
	}

	/**
	 * 注册一个事件
	 * @param {Object} event	事件
	 * @param {Object} handler 事件处理者
	 * @param {Boolean} single 此handler是否只处理一次
	 */
	async on(event, handler, single = false) {
		const eType = await this.getType(event);
		if (eType === "[object String]" && eType.trim() !== "") {
			if (this.on_register[event] == void 0) {
				this.on_register[event] = [];
			}

			if (single) {
				console.log('this.on_register[event]: ', this.on_register[event].length);
				for (let i = 0; i < this.on_register[event].length; i++) {
					console.log(handler === this.on_register[event][i]);
					if (handler === this.on_register[event][i]) {
						console.log('触发错误');
						throw new UniSocketError(`当前「${event}」事件已被注册...`);
					}
				}
			}

			// 注册事件
			this.on_register[event.trim()].push(handler);
		}
	}

	/**
	 * 移除指定注册的事件
	 * @param {Object} name 事件名称
	 */
	async removeEventByName(name) {
		return Promise.then(() => {
			delete this.on_register[name];
		});
	}

	/**
	 * 给缓存池添加记录
	 */
	async addBuffer(data = {}) {
		const da = JSON.stringify(data);
		this._buffer_register.push(data);
	}

	/**
	 * 获取缓存池
	 */
	async getBuffer() {
		return this._buffer_register;
	}

	/**
	 * 获取连接状态
	 * @return {number} 0 表示连接中，1表示连接成功，2表示重连中，3表示失败
	 */
	async getState() {
		return this.begin_reconnection ? 2 : this._connectioned ? 1 : this.isError ? 3 : 0;
	}

	/**
	 * 关闭当前socket
	 */
	async close() {
		this.closed = true;
		this.SocketTask && this._connectioned && this.SocketTask.close();
	}

	/**
	 * 发送消息
	 */
	async emit(event, data = {}) {
		if (
			this.getType(event) === "[object Object]" &&
			this.getType(event) === "[object String]"
		) {
			let e = data;
			data = event;
			event = e;
		}
		if (this.SocketTask) {
			const da = {
				type: event,
				data: data,
			};
			this.SocketTask.send({
				data: JSON.stringify(da),
				fail: (e) => {
					// 消息发送失败时将消息缓存
					this.addBuffer(da);
					throw new UniSocketError("Failed to send message to server... " + e);
				},
			});
		} else {
			throw new UniSocketError("The socket is not initialization or connection error!");
		}
	}
	// 绑定账号
	bindAccount(account) {
		console.log(account)
		if (this._connectioned) {
			// 缓存池备份
			let browser = {
				name: "Other",
				version: "1.0.0",
				appLanguage: 'zh-CN'
			};
			uni.getSystemInfo({
				success: (res) => {
					APP_VERSION = res.appVersion
					browser.version = res.osVersion
					browser.name = res.osName
					browser.appLanguage = res.appLanguage
					if (res.uniPlatform === 'web') {
						APP_CHANNEL = 'uni-h5'
						browser.version = res.hostVersion
						browser.name = res.hostName
					} else {
						if (res.osName === "android") {
							APP_CHANNEL = 'uni-android'
						}
						if (res.osName === "ios") {
							APP_CHANNEL = 'uni-ios'
						}
					}
				}
			});

			// '绑定账号' APP_CHANNEL
			uni.setStorageSync('account', String(account))
			let deviceId = uni.getStorageSync('deviceId')
			if (deviceId == "" || deviceId == undefined) {
				deviceId = this.generateUUID();
				uni.setStorageSync('deviceId', deviceId)
			}
			let body = new proto.com.farsunset.cim.sdk.web.model.SentBody();
			body.setKey("client_bind");
			body.setTimestamp(new Date().getTime());
			body.getDataMap().set("uid", String(account));
			body.getDataMap().set("channel", APP_CHANNEL);
			body.getDataMap().set("appVersion", APP_VERSION);
			body.getDataMap().set("osVersion", browser.version);
			body.getDataMap().set("packageName", APP_PACKAGE);
			body.getDataMap().set("deviceId", deviceId);
			body.getDataMap().set("deviceName", browser.name);
			body.getDataMap().set("language", browser.appLanguage);
			//绑定cid
			//#ifdef APP-PLUS
			let clientid = uni.getStorageSync("clientId")
			body.getDataMap().set("clientId", clientid);
			// #endif
			this.sendRequest(body)
		}

	}
	//绑定tag
	async bindRoomTag(tag) { 
		if (this._connectioned) {
			let body = new proto.com.farsunset.cim.sdk.web.model.SentBody();
			if (tag) {
				body.setKey("client_set_tag");
				body.getDataMap().set("tag", tag);
			} else body.setKey("client_remove_tag");
			this.sendRequest(body)
		}
	}
	// 发送缓存池数据
	async sendRequest(body) {
		let data = body.serializeBinary();
		let protobuf = new Uint8Array(data.length + 1);
		protobuf[0] = SENT_BODY;
		protobuf.set(data, 1);
		const buffer = protobuf
		this.SocketTask.send({
			data: buffer,
			success: (res) => {
				console.log('成功')
			},
		});
	}
	generateUUID() {
		let d = new Date().getTime();
		let uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
			let r = (d + Math.random() * 16) % 16 | 0;
			d = Math.floor(d / 16);
			return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
		});
		return uuid.replace(/-/g, '');
	}
	/**
	 * 发生错误
	 * @param {Object} callback
	 */
	async error(err) {
		this.isError = true;
		if (this.on_register["error"] !== undefined) {
			this.invokeHandlerFunctionOnRegistr("error", err);
		}
	}

	/**
	 * 重新连接错误
	 * @param {Object} err 错误信息
	 */
	async reconnectionError(err) {
		this.isError = true;
		if (this.on_register["reconnectionerror"] !== undefined) {
			this.invokeHandlerFunctionOnRegistr("reconnectionerror", err);
		}
	}

	/**
	 * 连接成功
	 */
	async connectioned() {
		this.isError = false;
		// 关闭重连状态
		this.begin_reconnection = false;
		this._connectioned = true;
		if (this.on_register["connectioned"] !== undefined) {
			this.invokeHandlerFunctionOnRegistr("connectioned");
		}
	}

	/**
	 * 开始发送心跳
	 */
	async beginSendHeartBeat() {
		this._heart_rate_interval = setInterval((res) => {
			this.emit(this._heart_rate_type);
			this.emitMessageToTargetEventByName("HEARTBEAT", {
				msg: "Send a heartbeat to the server...",
			});
		}, this._heart_rate);
	}

	/**
	 * 将心跳结束
	 */
	async killApp() {
		this._heart_rate_interval && clearInterval(this._heart_rate_interval);
	}

	/**
	 * 重连socket
	 */
	async reconnection() {
		// 处于与服务器断开状态并且不是被动断开
		this._connectioned = false;
		if (!this.closed) {
			this.reconnection_time = setTimeout(() => {
				this.begin_reconnection = true;
				this.connection();
			}, 1000);
		}
	}

	/**
	 * 初始化程序
	 */
	async init() {
		console.log('开始链接init');
		this.connection();
	}

	/**
	 * 连接socket
	 */
	async connection() {
		// 是否有重连任务
		if (this.reconnection_time) {
			console.log('clearTimeout')
			clearTimeout(this.reconnection_time);
		}
		/// 创建一个socket对象,返回socket连接
		const SocketTask = uni.connectSocket({
			url: this._url,
			success: () => {
				console.log('connectSocket-success')
			},
		});
		/// 打开连接的监听
		SocketTask.onOpen(() => {
			this.SocketTask = SocketTask;
			console.log('打开中')
			// 标记已成功连接socket
			this._connectioned = true;
			SocketTask.onClose(() => {
				// 重新连接
				if (!this.closed) {
					this.reconnection();
				}
			});
			this.connectioned();
		});

		SocketTask.onMessage((msg) => {
			// console.log(msg)
			const message = this.changeMsg(msg)
			if (message === false) return
			try {
				this.emitToClientAllEvent(message);
			} catch (e) {
				/// 服务器发来的不是一个标准的数据
				this.emitToClientNotNameEvents(message);
			}
		});

		/// 连接打开失败
		SocketTask.onError((res) => {
			// 不在重连状态
			if (!this.begin_reconnection) {
				this.error(res);
			} else {
				this.reconnectionError(res);
			}
			// 重新连接
			this.reconnection();
		});
	}
	changeMsg(e) { // 格式化消息
		let data = new Uint8Array(e.data);
		let type = data[0];
		let body = data.subarray(DATA_HEADER_LENGTH, data.length);
		if (type === PING) {
			let pong = new Uint8Array(PONG_BODY.byteLength + 1);
			pong[0] = PONG;
			pong.set(PONG_BODY, 1);
			// console.log('心跳')
			this.SocketTask.send({
				data: pong,
				fail: (e) => {
					throw new UniSocketError("Failed to send message to server... " + e);
				},
			});
			return false;
		}
		if (type == MESSAGE) {
			let message = proto.com.farsunset.cim.sdk.web.model.Message.deserializeBinary(body);
			// console.log(message)
			return message.toObject(false)
		}

		if (type == REPLY_BODY) {
			let message = proto.com.farsunset.cim.sdk.web.model.ReplyBody.deserializeBinary(body);
			// console.log(message)
			/**
			 * 将proto对象转换成json对象，去除无用信息
			 */
			let reply = {};
			reply.code = message.getCode();
			reply.key = message.getKey();
			reply.message = message.getMessage();
			reply.timestamp = message.getTimestamp();
			reply.data = {};

			/**
			 * 注意，遍历map这里的参数 value在前key在后
			 */
			message.getDataMap().forEach(function(v, k) {
				reply.data[k] = v;
			});
			return reply
		}
	}
	/**
	 * 注销监听
	 */
	off(event, handler) {
		const handlers = JSON.stringify(JSON.parse(this.on_register));
		for (let i = 0; i < handlers.length; i++) {
			if (handler === handlers[i]) {
				handlers.splice(i, 1);
			}
		}
		return this.off;
	}

	// async function handler

	/**
	 * 给指定的事件发送消息
	 * @param {Object} name 事件名称
	 */
	async emitMessageToTargetEventByName(name, data) {
		this.invokeHandlerFunctionOnRegistr(name, data);
	}

	/**
	 * 联系使用on(**)注册的事件
	 */
	async emitToClientNotNameEvents(msg) {
		this.invokeHandlerFunctionOnRegistr("**", msg);
	}

	/**
	 * 联系使用on(*)注册的事件
	 */
	async emitToClientAllEvent(data) {
		this.invokeHandlerFunctionOnRegistr("*", data);
	}

	/**
	 * 获取对象类型
	 * @param {Object} o 需要验证的对象
	 */
	async getType(o) {
		return Object.prototype.toString.call(o);
	}

	/**
	 * 给指定的事件发送数据
	 * @param {Object} register 事件
	 * @param {Object} data 需要发送的数据
	 */
	async invokeHandlerFunctionOnRegistr(register, data) {
		// console.log(data)
		if (this.on_register[register] !== undefined) {
			const eventList = this.on_register[register];
			for (var i = 0; i < eventList.length; i++) {
				const event = eventList[i];
				event(data);
			}
		}
	}
}

// 自定义Error
var __extends = (this && this.__extends) || (function() {
	var extendStatics = function(d, b) {
		extendStatics = Object.setPrototypeOf ||
			({
					__proto__: []
				}
				instanceof Array && function(d, b) {
					d.__proto__ = b;
				}) ||
			function(d, b) {
				for (var p in b)
					if (Object.prototype.hasOwnProperty.call(b, p)) d[p] = b[p];
			};
		return extendStatics(d, b);
	};
	return function(d, b) {
		if (typeof b !== "function" && b !== null)
			throw new TypeError("Class extends value " + String(b) + " is not a constructor or null");
		extendStatics(d, b);

		function __() {
			this.constructor = d;
		}
		d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
	};
})();
var UniSocketError = /** @class */ (function(_super) {
	__extends(UniSocketError, _super);

	function UniSocketError(message) {
		var _this = _super.call(this, message) || this;
		_this.name = 'UniSocketError';
		return _this;
	}
	return UniSocketError;
}(Error));