/*
 * Copyright 2013-2022 Xia Jun(3979434@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***************************************************************************************
 *                                                                                     *
 *                        Website : http://www.farsunset.com                           *
 *                                                                                     *
 ***************************************************************************************
 */
package com.farsunset.cim.coder.protobuf;

import com.farsunset.cim.constant.CIMConstant;
import com.farsunset.cim.constant.ChannelAttr;
import com.farsunset.cim.constant.DataType;
import com.farsunset.cim.exception.ReadInvalidTypeException;
import com.farsunset.cim.model.Pong;
import com.farsunset.cim.model.SentBody;
import com.farsunset.cim.model.proto.SentBodyProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 服务端接收消息路由解码，通过消息类型分发到不同的真正解码器
 */
public class AppMessageDecoder extends ByteToMessageDecoder {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf buffer, List<Object> queue) throws Exception {

		context.channel().attr(ChannelAttr.PING_COUNT).set(null);

		buffer.markReaderIndex();

		byte type = buffer.readByte();

		/*
		 * 收到意外数据
		 */
		if (DataType.PONG.getValue() != type && type != DataType.SENT.getValue()){
			/*
			 消费掉无效数据
			 */
			int length = buffer.readableBytes();
			if (length > 0){
				buffer.readBytes(new byte[length]);
			}
			logger.warn("收到意外的数据,type={},length ={}", type,length);
			return;
		}

		/*
		 * 消息体不足3位，发生断包情况
		 */
		if (buffer.readableBytes() < CIMConstant.DATA_HEADER_LENGTH) {
			buffer.resetReaderIndex();
			return;
		}


		byte lv = buffer.readByte();
		byte hv = buffer.readByte();
		int length = getContentLength(lv, hv);

		/*
		 * 发生断包情况，等待接收完成
		 */
		if (buffer.readableBytes() < length) {
			buffer.resetReaderIndex();
			return;
		}
		 
		byte[] content = new byte[length];

		buffer.readBytes(content);

		Object message = mappingMessageObject(content, type);

		queue.add(message);

	}

	private Object mappingMessageObject(byte[] data, byte type) throws com.google.protobuf.InvalidProtocolBufferException {

		if (DataType.SENT.getValue() == type) {
			SentBodyProto.Model bodyProto = SentBodyProto.Model.parseFrom(data);
			SentBody body = new SentBody();
			body.setData(bodyProto.getDataMap());
			body.setKey(bodyProto.getKey());
			body.setTimestamp(bodyProto.getTimestamp());

			return body;
		}

		return Pong.getInstance();
	}

	/**
	 * 解析消息体长度
	 * 最大消息长度为2个字节表示的长度，即为65535
	 * @param lv 低位1字节消息长度
	 * @param hv 高位1字节消息长度
	 * @return 消息的真实长度
	 */
	private int getContentLength(byte lv, byte hv) {
		int l = (lv & 0xff);
		int h = (hv & 0xff);
		return l | h << 8;
	}

}
