package com.sondertara.common.io.serialization;


import com.sondertara.common.exception.CodecException;
import com.sondertara.common.io.stream.FastByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Robert HG (254963746@qq.com) on 11/6/15.
 */
public class JdkSerializer implements Serializer {
    public static final JdkSerializer INSTANCE=new JdkSerializer();

    @Override
    public byte[] serialize(Object obj) throws CodecException {


        try (FastByteArrayOutputStream bos = new FastByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws CodecException {

        try (ByteArrayInputStream bin = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bin)) {
            Object obj = ois.readObject();
            return (T) obj;
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }

    @Override
    public byte id() {
        return 0;
    }

}
