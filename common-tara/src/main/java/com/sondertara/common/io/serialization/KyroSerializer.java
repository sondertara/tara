package com.sondertara.common.io.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.sondertara.common.exception.CodecException;
import com.sondertara.common.io.stream.FastByteArrayOutputStream;
import de.javakaffee.kryoserializers.GregorianCalendarSerializer;
import de.javakaffee.kryoserializers.JdkProxySerializer;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.function.Consumer;

public class KyroSerializer implements Serializer {

    public static final KyroSerializer INSTANCE=new KyroSerializer();


    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(
            () -> {
                Kryo kryo = new Kryo();
                register(kryo);
                return kryo;
            });


    @Override
    public <T> byte[] serialize(T obj) throws CodecException {
        try (FastByteArrayOutputStream bos = new FastByteArrayOutputStream(); Output output = new Output(bos)) {
            KRYO_THREAD_LOCAL.get().writeObject(output, obj);
            return output.toBytes();
        } catch (Exception e) {
            throw new CodecException(e);
        } finally {
            KRYO_THREAD_LOCAL.remove();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> tClass) throws CodecException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data); Input input = new Input(bis)) {
            T object = KRYO_THREAD_LOCAL.get().readObject(input, tClass);
            return tClass.cast(object);
        } catch (Exception e) {
            throw new CodecException(e);
        } finally {
            KRYO_THREAD_LOCAL.remove();
        }
    }

    @Override
    public byte id() {
        return 3;
    }

    /**
     * 自定义
     *
     * @param consumer
     */

    public static void customizer(Consumer<Kryo> consumer) {
        consumer.accept(KRYO_THREAD_LOCAL.get());
    }


    public static void register(Kryo kryo) {
        kryo.setRegistrationRequired(false);
        // 增加以下类型的自定义序列化实现
        kryo.register(Arrays.asList("").getClass(), new DefaultSerializers.ArraysAsListSerializer());
        kryo.register(Collections.EMPTY_LIST.getClass(), new DefaultSerializers.CollectionsEmptyListSerializer());
        kryo.register(Collections.EMPTY_MAP.getClass(), new DefaultSerializers.CollectionsEmptyMapSerializer());
        kryo.register(Collections.EMPTY_SET.getClass(), new DefaultSerializers.CollectionsEmptySetSerializer());
        kryo.register(Collections.singletonList("").getClass(), new DefaultSerializers.CollectionsSingletonListSerializer());
        kryo.register(Collections.singleton("").getClass(), new DefaultSerializers.CollectionsSingletonSetSerializer());
        kryo.register(Collections.singletonMap("", "").getClass(), new DefaultSerializers.CollectionsSingletonMapSerializer());
        kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
        kryo.register(UUID.class, new DefaultSerializers.UUIDSerializer());
        kryo.register(InvocationHandler.class, new JdkProxySerializer());
        UnmodifiableCollectionsSerializer.registerSerializers(kryo);
        SynchronizedCollectionsSerializer.registerSerializers(kryo);

    }
}
