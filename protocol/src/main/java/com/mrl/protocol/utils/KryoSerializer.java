package com.mrl.protocol.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.BeanSerializer;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
/**
 * @program: com.mrl.netty.common.utils
 * @description: 基于kryo的序列化/反序列化工具
 * @author:
 * @create: 2020-02-02 11:33
 **/
public class KryoSerializer implements Serializer {
    private final Class<?> ct;
    // kryo 是非线程安全类
    final ThreadLocal<Kryo> kryoLocal = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.register(ct, new BeanSerializer(kryo, ct));
            return kryo;
        }
    };

    public KryoSerializer(Class<?> ct) {
        this.ct = ct;
    }

    public Class<?> getCt() {
        return ct;
    }

    private Kryo getKryo() {
        return kryoLocal.get();
    }



    @Override
    public byte[] serialize(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Output output = new Output(bos);
        try {
            Kryo kryo = getKryo();
            kryo.writeObjectOrNull(output, obj, obj.getClass());
            output.flush();
            return bos.toByteArray();
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(bos);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes) {
        if (bytes == null)
            return null;
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Input input = new Input(bais);
        try {
            Kryo kryo = getKryo();
            return (T) kryo.readObjectOrNull(input, ct);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(bais);
        }
    }

}
