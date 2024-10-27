package com.sondertara.common.io.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sondertara.common.exception.CodecException;
import com.sondertara.common.json.JsonUtils;

import java.io.IOException;

/**
 * @author huangxiaohu
 */
public class JacksonSerializer implements Serializer {
    public static final JacksonSerializer INSTANCE = new JacksonSerializer();

    @Override
    public <T> byte[] serialize(T obj) throws CodecException {
        ObjectMapper mapper = JsonUtils.getMapper();
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> tClass) throws CodecException {
        try {
            return JsonUtils.getMapper().readValue(data, tClass);
        } catch (IOException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public byte id() {
        return 2;
    }
}
