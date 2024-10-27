package com.sondertara.common.io;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class IODebugger {
    public static String showBytes(byte[] bytes) {
        return showBytes(bytes, null);
    }

    public static String showBytes(@NonNull byte[] bytes, @Nullable Charset charset) {
        Objects.requireNonNull(bytes);
        return charset == null ? new String(bytes, StandardCharsets.UTF_8) : new String(bytes, charset);
    }


    public static String showBytes(ByteBuffer byteBuffer) {
        return showBytes(byteBuffer, null);
    }

    public static String showBytes(ByteBuffer byteBuffer, Charset charset) {
        if (byteBuffer.remaining() > 0) {
            int position = byteBuffer.position();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            byteBuffer.position(position);
            return showBytes(bytes, charset);
        }
        return "";
    }

    public static String showBytes(@NonNull InputStream inputStream) {
        return showBytes(inputStream, null);
    }

    public static String showBytes(@NonNull InputStream inputStream, @NonNull Charset charset) {
        try {
            return IOUtils.readAsString(inputStream, charset);
        }catch (IOException e){
            return "IO ERROR";
        }
    }

    private IODebugger() {

    }
}
