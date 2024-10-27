
package com.sondertara.common.io.serialization;


/**
 * 全局序列化管理器
 *
 * @author huangxiaohu
 */
public class SerializerManager {

    private static Serializer[] serializers = new Serializer[5];


    static {
        addSerializer(JdkSerializer.INSTANCE.id(), JdkSerializer.INSTANCE);
        addSerializer(ProtostuffSerializer.INSTANCE.id(), ProtostuffSerializer.INSTANCE);
        addSerializer(JacksonSerializer.INSTANCE.id(), JacksonSerializer.INSTANCE);
        //addSerializer(KyroSerializer.INSTANCE.id(), KyroSerializer.INSTANCE);
        //addSerializer(HessianSerializer.INSTANCE.id(), HessianSerializer.INSTANCE);
    }

    public static Serializer getSerializer(int idx) {
        if (idx > serializers.length || idx < 0) {
            throw new IllegalArgumentException("Idx is out of range max is:" + (serializers.length - 1));
        }
        return serializers[idx];
    }

    public static void addSerializer(int idx, Serializer serializer) {
        if (serializers.length <= idx) {
            Serializer[] newSerializers = new Serializer[idx + 5];
            System.arraycopy(serializers, 0, newSerializers, 0, serializers.length);
            serializers = newSerializers;
        }
        serializers[idx] = serializer;
    }
}
