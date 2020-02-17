package com.mrl.protocol.pojo.message;

/**
 * @program: com.mrl.netty.common.pojo.message
 * @description: message
 * @author:
 * @create: 2020-02-01 15:22
 **/
public class Message {

    public static final Message HEART_BEAT = new Message(
            new MessageHeader(MessageType.HEART_BEAT), null
    );
    public static final Message SUBMIT = new Message(
            new MessageHeader(MessageType.TASK_SUBMIT),
            new MessageContent<String>()
    );
    public static final Message TASK_GET = new Message(
            new MessageHeader(MessageType.TASK_GET), null
    );
    /**
     * 消息头
     */
    private MessageHeader messageHeader;

    /**
     * 消息内容
     */
    private MessageContent messageContent;

    public Message() {
    }

    public Message(MessageHeader messageHeader, MessageContent messageContent) {
        this.messageHeader = messageHeader;
        this.messageContent = messageContent;
    }

    public MessageHeader getMessageHeader() {
        return messageHeader;
    }

    public void setMessageHeader(MessageHeader messageHeader) {
        this.messageHeader = messageHeader;
    }

    public MessageContent getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(MessageContent messageContent) {
        this.messageContent = messageContent;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageHeader=" + messageHeader +
                ", messageContent=" + messageContent +
                '}';
    }
}
