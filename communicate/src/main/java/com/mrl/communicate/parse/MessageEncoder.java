package com.mrl.communicate.parse;

import com.mrl.protocol.factory.SerializerFactory;
import com.mrl.protocol.pojo.message.Message;
import com.mrl.protocol.utils.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @program: com.mrl.netty.common.parse
 * @description: 自定义kryo编码器(将传输对象变为byte数组)
 * @author:
 * @create: 2020-02-02 11:40
 **/
public class MessageEncoder extends MessageToByteEncoder<Message> {

    private Serializer serializer = SerializerFactory.getSerializer(Message.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 1. 将对象转换为byte
        byte[] body = serializer.serialize(msg);
        // 2. 读取消息的长度
        int dataLength = body.length;
        // 3. 先将消息长度写入，也就是消息头
        out.writeInt(dataLength);
        out.writeBytes(body);
    }
}
