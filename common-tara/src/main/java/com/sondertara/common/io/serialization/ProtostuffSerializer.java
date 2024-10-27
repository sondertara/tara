
package com.sondertara.common.io.serialization;

import com.sondertara.common.exception.CodecException;
import io.protostuff.ByteArrayInput;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffOutput;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;


/**
 * Protostuff 序列化工具
 *
 * @author huangxiaohu
 */
public class ProtostuffSerializer implements Serializer {
    public static final ProtostuffSerializer INSTANCE = new ProtostuffSerializer();
    private static final ThreadLocal<LinkedBuffer> LINKED_BUFFER_THREAD_LOCAL = ThreadLocal.withInitial(
            () -> LinkedBuffer.allocate(512));


    @SuppressWarnings("unchecked")
    @Override
    public <T> byte[] serialize(T obj) throws CodecException {
        final Schema<T> schema = RuntimeSchema.getSchema((Class<T>) obj.getClass());

        LinkedBuffer linkedBuffer = LINKED_BUFFER_THREAD_LOCAL.get();
        ProtostuffOutput output = new ProtostuffOutput(linkedBuffer);
        try {
            schema.writeTo(output, obj);
            return output.toByteArray();
        } catch (final Exception e) {
            throw new CodecException(e);
        } finally {
            LINKED_BUFFER_THREAD_LOCAL.remove(); // for reuse
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> tClass) throws CodecException {
        final Schema<T> schema = RuntimeSchema.getSchema(tClass);
        final T msg = schema.newMessage();

        final ByteArrayInput input = new ByteArrayInput(data, 0, data.length, true);
        try {
            schema.mergeFrom(input, msg);
            input.checkLastTagWas(0);
            return msg;
        } catch (final Exception e) {
            throw new CodecException(e);
        }
    }

    @Override
    public byte id() {
        return 1;
    }
}
