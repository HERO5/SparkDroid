package com.mrl.protocol.pojo.message;

/**
 * @program: com.mrl.netty.common.pojo.message
 * @description: 消息内容泛型类
 * @author:
 * @create: 2020-02-01 15:22
 **/
public class MessageContent<T> {
    private T content;

    public MessageContent() {
    }

    public MessageContent(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MessageContent{" +
                "content=" + content +
                '}';
    }
}
