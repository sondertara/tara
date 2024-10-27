package com.sondertara.common.json;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.sondertara.common.datetime.DatePattern;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author huangxiaohu
 */
public class JsonUtils {
    private static volatile ObjectMapper MAPPER;

    private JsonUtils() {
        throw new IllegalArgumentException("Utils not allow instance");
    }


    public static <T> String toJsonString(T src) {
        try {
            return src instanceof String ? (String) src : getInstance().writeValueAsString(src);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> String toJsonString(T src, Include inclusion) {
        if (src instanceof String) {
            return (String) src;
        } else {
            ObjectMapper customMapper = generateMapper(inclusion);
            try {
                return customMapper.writeValueAsString(src);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public static <T> String toJsonString(T src, ObjectMapper mapper) {
        if (null != mapper) {
            try {
                return src instanceof String ? (String) src : mapper.writeValueAsString(src);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException(e);
            }
        } else {
            return null;
        }
    }

    public static ObjectMapper getMapper() {
        return getInstance().copy();
    }

    private static ObjectMapper getInstance() {
        if (null == MAPPER) {
            synchronized (JsonUtils.class) {
                if (null == MAPPER) {
                    MAPPER = generateMapper(Include.ALWAYS);
                }
            }
        }

        return MAPPER;
    }

    private static ObjectMapper generateMapper(Include include) {
        ObjectMapper customMapper = new ObjectMapper();
        customMapper.setSerializationInclusion(include);
        //取消默认转换timestamps形式
        customMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //忽略空Bean转json的错误
        customMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        customMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        customMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        customMapper.configure(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature(), true);
        customMapper.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true);
        customMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        customMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        customMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
        customMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DatePattern.NORM_DATETIME_FORMATTER));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DatePattern.NORM_DATE_FORMATTER));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DatePattern.NORM_TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DatePattern.NORM_DATETIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DatePattern.NORM_DATE_FORMATTER));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DatePattern.NORM_TIME_FORMATTER));
        customMapper.registerModule(javaTimeModule);
        customMapper.setTimeZone(TimeZone.getDefault());
        customMapper.findAndRegisterModules();
        return customMapper;
    }


    public static JavaType makeJavaType(Class<?> parametrized, Class<?>... parameterClasses) {
        return getInstance().getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }

    public static JavaType makeJavaType(Class<?> rawType, JavaType... parameterTypes) {
        return getInstance().getTypeFactory().constructParametricType(rawType, parameterTypes);
    }

    public static String toString(Object value) {
        if (Objects.isNull(value)) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return toJsonString(value);
    }


    @SneakyThrows
    public static String toPrettyString(Object value) {
        return getInstance().writerWithDefaultPrettyPrinter().writeValueAsString(value);
    }

    @SneakyThrows
    public static JsonNode fromJavaObject(Object value) {
        JsonNode result = null;
        if (Objects.nonNull(value) && (value instanceof String)) {
            result = parseObject((String) value);
        } else {
            result = getInstance().valueToTree(value);
        }
        return result;
    }

    /**
     * JsonNode和JSONObject一样，都是JSON树形模型，只不过在jackson中，存在的是JsonNode
     */
    @SneakyThrows
    public static JsonNode parseObject(String content) {
        return getInstance().readTree(content);
    }


    public static ArrayNode parseArray(String content) {
        JsonNode jsonNode = parseObject(content);
        if (jsonNode.getNodeType().equals(JsonNodeType.ARRAY)) {
            return (ArrayNode) jsonNode;
        }
        throw new IllegalArgumentException("The json is not array");
    }


    public static JsonNode getJsonElement(JsonNode node, String name) {
        return node.get(name);
    }

    public static JsonNode getJsonElement(JsonNode node, int index) {
        return node.get(index);
    }

    @SneakyThrows
    public static <T> T toJavaObject(TreeNode node, Class<T> clazz) {
        return getInstance().treeToValue(node, clazz);
    }

    @SneakyThrows
    public static <T> T toJavaObject(TreeNode node, JavaType javaType) {
        return getInstance().convertValue(node, javaType);
    }

    @SneakyThrows
    public static <T> T toJavaObject(TreeNode node, TypeReference<T> typeReference) {
        return getInstance().convertValue(node, typeReference);
    }

    public static <T> T toJavaObject(TreeNode node, Type type) {
        return toJavaObject(node, getInstance().constructType(type));
    }

    public static <E> List<E> toJavaList(TreeNode node, Class<E> clazz) {
        return toJavaObject(node, makeJavaType(List.class, clazz));
    }

    public static List<Object> toJavaList(TreeNode node) {
        return toJavaObject(node, new TypeReference<List<Object>>() {
        });
    }

    public static <V> Map<String, V> toJavaMap(TreeNode node, Class<V> clazz) {
        return toJavaObject(node, makeJavaType(Map.class, String.class, clazz));
    }

    public static Map<String, Object> toJavaMap(TreeNode node) {
        return toJavaObject(node, new TypeReference<Map<String, Object>>() {
        });
    }

    @SneakyThrows
    public static <T> T toJavaObject(String content, Class<T> clazz) {
        return getInstance().readValue(content, clazz);
    }

    @SneakyThrows
    public static <T> T toJavaObject(String content, JavaType javaType) {
        return getInstance().readValue(content, javaType);
    }

    @SneakyThrows
    public static <T> T toJavaObject(String content, TypeReference<T> typeReference) {
        return getInstance().readValue(content, typeReference);
    }

    public static <T> T toJavaObject(String content, Type type) {
        return toJavaObject(content, getInstance().constructType(type));
    }

    public static <E> List<E> toJavaList(String content, Class<E> clazz) {
        return toJavaObject(content, makeJavaType(List.class, clazz));
    }

    public static List<Object> toJavaList(String content) {
        return toJavaObject(content, new TypeReference<List<Object>>() {
        });
    }

    public static <V> Map<String, V> toJavaMap(String content, Class<V> clazz) {
        return toJavaObject(content, makeJavaType(Map.class, String.class, clazz));
    }

    public static Map<String, Object> toJavaMap(String content) {
        return toJavaObject(content, new TypeReference<Map<String, Object>>() {
        });
    }


    /**
     * ===========================以下是从JSON中获取对象====================================
     */
    public static <T> T parseObject(String jsonString, Class<T> object) {
        T t = null;
        try {
            t = getInstance().readValue(jsonString, object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        return t;
    }

    public static <T> T parseObject(File file, Class<T> object) {
        T t = null;
        try {
            t = getInstance().readValue(file, object);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return t;
    }


    /**
     * =================================以下是将对象转为JSON=====================================
     */

    public static byte[] toByteArray(Object object) {
        byte[] bytes = null;
        try {
            bytes = getInstance().writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        return bytes;
    }

    public static void objectToFile(Object object, File file) {
        try {
            getInstance().writeValue(file, object);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    /**
     * =============================以下是与JsonNode相关的=======================================
     */


    public static String toJsonString(JsonNode jsonNode) {
        String jsonString = null;
        try {
            jsonString = getInstance().writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        return jsonString;
    }

    /**
     * JsonNode是一个抽象类，不能实例化，创建JSON树形模型，得用JsonNode的子类ObjectNode，用法和JSONObject大同小异
     */

    public static ObjectNode newJsonObject() {
        return getInstance().createObjectNode();
    }

    /**
     * 创建JSON数组对象，就像JSONArray一样用
     *
     * @return
     */
    public static ArrayNode newJsonArray() {
        return getInstance().createArrayNode();
    }


}