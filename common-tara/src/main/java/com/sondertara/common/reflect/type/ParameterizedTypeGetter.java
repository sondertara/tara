package com.sondertara.common.reflect.type;

import com.sondertara.common.reflect.ReflectUtils;
import com.sondertara.common.text.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Usage:
 * <p>
 * <pre>
 *  List:
 *  ParameterizedType type0 = new ParameterizedTypeGetter<List<Person>>(){}.getType();
 *  ParameterizedType type1 = Types.getParameterizedType(List.class, Person.class);
 *  ParameterizedType type2 = new ParameterizedType(null, List.class, Person.class);
 *
 *  type0 equivalent to type1
 *  type0 equivalent to type2
 *
 * </pre>
 *
 * @param <T>
 */
public abstract class ParameterizedTypeGetter<T> {
    private Type rawType;
    private Type[] actualTypeArguments;

    protected ParameterizedTypeGetter() {
        parseSuperclassTypeParameter(getClass());
    }

    private ParameterizedTypeGetter(Type type) {
        this.rawType = type;

        if (TypeUtils.isParameterizedType(type)) {
            this.actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            this.rawType = ((ParameterizedType) type).getRawType();
        }
    }

    private void parseSuperclassTypeParameter(Class<?> clazz) {
        // 参数 clazz 本质是 ParameterizedTypeGetter 的一个子类
        // genericSuperclass 就是 ParameterizedTypeGetter 的泛型形态
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof Class) {
            throw new RuntimeException(StringUtils.format("{} is not a parameterized type", ReflectUtils.getFQNClassName(clazz)));
        }

        this.actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        if (actualTypeArguments.length > 0) {
            Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
            if (rawType instanceof ParameterizedType) {
                rawType = ((ParameterizedType) rawType).getRawType();
            }
            this.rawType = rawType;
        } else {
            this.rawType = clazz;
        }

    }

    public static ParameterizedTypeGetter forType(Type type) {
        return new ParameterizedTypeGetter(type) {
        };
    }

    /**
     * 外层类型
     *
     */
    public final Type getRawType() {
        return rawType;
    }

    public final Type getActualArgumentType(int index) {
        int length = actualTypeArguments == null ? 0 : actualTypeArguments.length;
        if(actualTypeArguments != null && index>=0 && index<length ){
            return actualTypeArguments[index];
        }
        else{
           throw new IllegalArgumentException(StringUtils.format("illegal index: {}", index));
        }

    }

    public final Type getType() {
        return getActualArgumentType(0);
    }

    @Override
    public String toString() {
        return rawType.toString();
    }
}
