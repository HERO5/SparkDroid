package com.mrl.protocol.factory;

import com.mrl.protocol.utils.KryoSerializer;
import com.mrl.protocol.utils.Serializer;

/**
 * @program: com.mrl.protocol.factory
 * @description: 序列化工具类工厂实现
 * @author:
 * @create: 2020-02-02 11:38
 **/
public class SerializerFactory {

    public static Serializer getSerializer(Class<?> cls) {
        return new KryoSerializer(cls);
    }
}
