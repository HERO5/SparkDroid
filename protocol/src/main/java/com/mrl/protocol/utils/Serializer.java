package com.mrl.protocol.utils;

/**
 * @program: com.mrl.netty.common.utils
 * @description: 自定义序列化接口
 * @author:
 * @create: 2020-02-02 11:22
 **/
public  interface Serializer {
    /**
     * 序列化
     * @param obj
     */
    byte[] serialize(Object obj);


    /**
     * 反序列化
     * @param bytes 字节数组
     * @return
     */
    <T> T deserialize(byte[] bytes);
}
