package com.sondertara.common.io;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.io.stream.obj.SecureObjectInputStream;
import com.sondertara.common.reflect.ReflectUtils;
import com.sondertara.common.text.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 *  */
public class ObjectIOs {
    private ObjectIOs() {
    }

    /**
     * 使用 JDK 的 output stream 写对象。
     * 在序列化时，不会对 static 字段， transient 字段序列化
     *
     * @param obj 必须实现 Serializable 接口
     */
    public static <T> byte[] serialize(T obj) throws IOException {
        if (obj == null) {
            return Emptys.EMPTY_BYTES;
        }
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try {
            serialize(obj, bao);
            return bao.toByteArray();
        } finally {
            IOUtils.close(bao);
        }
    }

    /**
     * 序列化到指定的输出流
     */
    public static <T> void serialize(T obj, @NonNull OutputStream outputStream) throws IOException {
        if (obj == null) {
            return;
        }
        Objects.requireNonNull(outputStream, "the output stream is null");
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(outputStream);
            output.writeObject(obj);
            output.flush();
        } finally {
            IOUtils.close(output);
        }
    }

    public static <T> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        return deserialize(bytes, null);
    }

    public static <T> T deserialize(byte[] bytes, @Nullable Class<T> targetType) throws IOException, ClassNotFoundException {
        if (Emptys.isEmpty(bytes)) {
            return null;
        }
        ObjectInputStream input = null;
        ByteArrayInputStream bai = null;
        try {
            bai = new ByteArrayInputStream(bytes);
            input = new SecureObjectInputStream(bai);
            Object obj = input.readObject();
            if (obj == null) {
                return null;
            }
            if (targetType != null) {
                if (targetType.isInstance(obj)) {
                    return (T) obj;
                }
                throw new ClassCastException(StringUtils.format("Class {} is not been cast to {}", ReflectUtils.getFQNClassName(obj.getClass()), ReflectUtils.getFQNClassName(targetType)));
            } else {
                return (T) obj;
            }
        } finally {
            IOUtils.close(bai);
            IOUtils.close(input);
        }

    }
}
