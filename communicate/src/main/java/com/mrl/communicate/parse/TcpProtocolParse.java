package com.mrl.communicate.parse;

import android.util.Log;

import com.mrl.protocol.pojo.message.Message;
import com.mrl.protocol.pojo.message.MessageHeader;
import com.mrl.protocol.utils.ByteConvertUtil;

/**
 * @program: com.mrl.protocal.parse
 * @description:
 * @author:
 * @create: 2020-02-01 15:10
 **/

public class TcpProtocolParse {

    public static Message parse(byte[] msg) {

        Log.d("TcpProtocolParse", "消息数据: " + ByteConvertUtil.bytesToHEXString(msg));

        /**
         * 消息头解析
         */
        MessageHeader messageHeader = messageHeaderParse(msg);
        Message message = new Message();
        message.setMessageHeader(messageHeader);
        //message.setMessageContent();
        Log.d("TcpProtocolParse", "解析完成！"+message.toString());
        return message;
    }

    /**
     * 消息头解析
     *
     * @param msg 消息数据
     * @return 消息头
     */
    public static MessageHeader messageHeaderParse(byte[] msg) {
        MessageHeader messageHeader = new MessageHeader();

        messageHeader.setLength(ByteConvertUtil.bytesToUnSignedIntBE(msg, 0));
        messageHeader.setMessageType(ByteConvertUtil.bytesToUnSignedShortBE(msg, 4));
        messageHeader.setTransportId(ByteConvertUtil.bytesToUnSignedShortBE(msg, 6));
        messageHeader.setVersion(ByteConvertUtil.bytesToUnSignedByteBE(msg, 8));
        messageHeader.setReserve1(ByteConvertUtil.bytesToUnSignedByteBE(msg, 9));
        messageHeader.setReserve2(ByteConvertUtil.bytesToUnSignedShortBE(msg, 10));
        return messageHeader;
    }
}
