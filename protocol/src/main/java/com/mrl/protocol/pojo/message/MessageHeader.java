package com.mrl.protocol.pojo.message;

/**
 * @program: com.mrl.netty.common.pojo
 * @description:
 * @author:
 * @create: 2020-02-01 15:18
 **/
public class MessageHeader {
    /**
     * 消息长度，消息头和消息内容的字节数。无符号4字节
     */
    private long length;
    /**
     * 消息类型。无符号2字节
     */
    private int messageType;
    /**
     * 消息流水号。响应消息需要回填请求消息的流水号。无符号2字节
     */
    private int transportId;
    /**
     * 接口版本号。目前使用的为0x22。无符号1字节
     */
    private short version;
    /**
     * 保留字段1。无符号1字节
     */
    private short reserve1;
    /**
     * 保留字段2。无符号2字节
     */
    private int reserve2;

    public MessageHeader() {
    }

    public MessageHeader(int messageType) {
        this.messageType = messageType;
    }

    public MessageHeader(long length, int messageType, int transportId, short version, short reserve1, int reserve2) {
        this.length = length;
        this.messageType = messageType;
        this.transportId = transportId;
        this.version = version;
        this.reserve1 = reserve1;
        this.reserve2 = reserve2;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getTransportId() {
        return transportId;
    }

    public void setTransportId(int transportId) {
        this.transportId = transportId;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public short getReserve1() {
        return reserve1;
    }

    public void setReserve1(short reserve1) {
        this.reserve1 = reserve1;
    }

    public int getReserve2() {
        return reserve2;
    }

    public void setReserve2(int reserve2) {
        this.reserve2 = reserve2;
    }

    @Override
    public String toString() {
        return "MessageHeader{" +
                "length=" + length +
                ", messageType=" + messageType +
                ", transportId=" + transportId +
                ", version=" + version +
                ", reserve1=" + reserve1 +
                ", reserve2=" + reserve2 +
                '}';
    }
}
