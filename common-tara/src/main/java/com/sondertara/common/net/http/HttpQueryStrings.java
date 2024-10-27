package com.sondertara.common.net.http;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.codec.CodecException;
import com.sondertara.common.codec.base64.Base64;
import com.sondertara.common.collection.ArrayUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.StringMap;
import com.sondertara.common.collection.multivalue.LinkedMultiValueMap;
import com.sondertara.common.collection.multivalue.MultiValueMap;
import com.sondertara.common.collection.stack.SimpleStack;
import com.sondertara.common.collection.stack.Stack;
import com.sondertara.common.reflect.type.Primitives;
import com.sondertara.common.struct.Entry;
import com.sondertara.common.text.StringJoiner;
import com.sondertara.common.text.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class HttpQueryStrings {
    private HttpQueryStrings(){

    }
    public static StringMap getQueryStringStringMap(String url) {
        if (url == null) {
            return StringMap.EMPTY;
        }
        int paramPartStartIndex = url.indexOf("?") + 1;
        if (paramPartStartIndex == 0 || paramPartStartIndex == url.length()) {
            return StringMap.EMPTY;
        }
        int paramPartEndIndex = url.indexOf("#");
        String queryString = paramPartEndIndex == -1 ? url.substring(paramPartStartIndex) : url.substring(paramPartStartIndex, paramPartEndIndex);
        return new StringMap(queryString, "=", "&");
    }

    public static MultiValueMap<String, String> getQueryStringMultiValueMap(String url) {
        if (url == null) {
            return LinkedMultiValueMap.EMPTY;
        }
        int paramPartStartIndex = url.indexOf("?") + 1;
        if (paramPartStartIndex == 0 || paramPartStartIndex == url.length()) {
            return LinkedMultiValueMap.EMPTY;
        }
        int paramPartEndIndex = url.indexOf("#");
        String queryString = paramPartEndIndex == -1 ? url.substring(paramPartStartIndex) : url.substring(paramPartStartIndex, paramPartEndIndex);
        return Entry.getMultiValueMap(queryString, "=", "&");
    }

    /**
     * 不会对URL中的特殊字符做处理
     *
     */
    public static String toQueryString(Map<String, Object> map, final Map<Class, Function<Object, String>> converterMap) {
        return toQueryString(map, true, null, converterMap);
    }

    public static String toQueryString(Map<String, Object> map, boolean encode, BiFunction<String, String, String> keyMapper, final Map<Class, Function<Object, String>> converterMap) {
        MultiValueMap<String, String> multiValueMap = toMultiValueMap(map, keyMapper, converterMap);
        return toQueryString(multiValueMap, encode);
    }

    public static String toQueryString(Map<String, String> map) {
        return toQueryString(map, false);
    }

    /**
     * 不会对URL中的特殊字符做处理，如需处理，请调用 UrlEncoder 类
     *
     */
    public static String toQueryString(Map<String, String> map, final boolean encode) {
        final StringJoiner joiner = new StringJoiner("&", "", "");

        CollectionUtils.forEach(map, new BiConsumer<String, String>() {
            @Override
            public void accept(final String key, String value) {
                if (encode) {
                    try {
                        value = URLEncoder.encode(value, StandardCharsets.UTF_8.name());
                    } catch (Throwable ex) {
                        throw new CodecException(ex);
                    }
                }
                joiner.add(StringUtils.format("{}={}", key, value));
            }
        });

        return joiner.toString();
    }

    public static String toQueryString(MultiValueMap<String, String> map) {
        return toQueryString(map, false);
    }

    public static String toQueryString(MultiValueMap<String, String> map, final boolean encode) {
        final StringJoiner joiner = new StringJoiner("&", "", "");

        CollectionUtils.forEach(map, new BiConsumer<String, Collection<String>>() {
            @Override
            public void accept(final String key, Collection<String> values) {
                CollectionUtils.forEach(values, new Consumer<String>() {
                    @Override
                    public void accept(String value) {
                        if (encode) {
                            try {
                                value = URLEncoder.encode(value, StandardCharsets.UTF_8.name());
                            } catch (Throwable ex) {
                                throw new CodecException(ex);
                            }
                        }
                        joiner.add(StringUtils.format("{}={}", key, value));
                    }
                });
            }
        });

        return joiner.toString();
    }

    private static MultiValueMap<String, String> toMultiValueMap(Map<String, Object> map, BiFunction<String, String, String> keyMapper, final Map<Class, Function<Object, String>> converterMap) {
        // 当值为 Object, Array 时，会把值放到 keyPrefixStack 中
        final Stack<String> keyPrefixStack = new SimpleStack<String>();
        keyPrefixStack.push("");
        final MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<String, String>();
        final BiFunction<String, String, String> keyBuilder = keyMapper == null ? new BiFunction<String, String, String>() {
            @Override
            public String apply(String keyPrefix, String key) {
                if (StringUtils.isEmpty(keyPrefix)) {
                    return key;
                }
                if (StringUtils.isEmpty(key)) {
                    return keyPrefix;
                }
                return keyPrefix + "." + key;
            }
        } : keyMapper;
        BiConsumer<String, Object> consumer = new BiConsumer<String, Object>() {
            @Override
            public void accept(final String key, Object value) {
                String prefix = keyPrefixStack.peek();
                String handledValue = null;
                if (value != null) {

                    String handledKey = keyBuilder.apply(prefix, key);
                    keyPrefixStack.push(handledKey);

                    Class valueClass = value.getClass();
                    if (converterMap != null && converterMap.containsKey(valueClass)) {
                        Function<Object, String> converter = converterMap.get(valueClass);
                        handledValue = converter.apply(value);
                    } else {
                        if (Primitives.isPrimitiveOrPrimitiveWrapperType(valueClass)) {
                            if (Primitives.isPrimitive(valueClass)) {
                                handledValue = "" + value;
                            } else {
                                handledValue = value.toString();
                            }
                        } else if (byte[].class == valueClass) {
                            handledValue = Base64.encodeBase64String((byte[]) value);
                        } else if (ArrayUtils.isArray(value)) {
                            final BiConsumer<String, Object> consumer = this;
                            CollectionUtils.forEach(value, new BiConsumer<Integer, Object>() {
                                @Override
                                public void accept(Integer index, Object element) {
                                    consumer.accept("", element);
                                }
                            });
                        } else if (value instanceof Map) {
                            CollectionUtils.forEach((Map) value, this);
                        } else {
                            handledValue = value.toString();
                        }
                    }
                    if (handledValue != null) {
                        multiValueMap.add(handledKey, handledValue);
                    }
                    keyPrefixStack.pop();
                }
            }
        };
        CollectionUtils.forEach(map, consumer);
        return multiValueMap;
    }

}
