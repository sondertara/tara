package com.sondertara.common.reflect.type;

import com.sondertara.common.base.Assert;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.base.Valid;
import com.sondertara.common.collection.ArrayUtils;
import com.sondertara.common.collection.Maps;
import com.sondertara.common.function.Builder;
import com.sondertara.common.reflect.ActualTypeMapperPool;
import com.sondertara.common.reflect.ClassUtils;
import com.sondertara.common.reflect.signature.TypeSignatures;
import com.sondertara.common.text.StringUtils;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * 针对 {@link Type} 的工具类封装<br>
 * 最主要功能包括：
 *
 * <pre>
 * 1. 获取方法的参数和返回值类型（包括Type和Class）
 * 2. 获取泛型参数类型（包括对象的泛型参数或集合元素的泛型类型）
 * </pre>
 *
 * @author huangxiaohu
 */
public class TypeUtils {
    private static final Type[] EMPTY_TYPE_ARRAY = new Type[]{};

    /**
     * judge a type is a primitive type or not
     */
    public static boolean isPrimitive(Type type) {
        return Primitives.isPrimitive(type);
    }

    /**
     * judge a type is a array or not
     */
    public static boolean isArray(Type type) {
        return isClass(type) && ((Class) type).isArray();
    }

    /**
     * judge a type is a class or not
     */
    public static boolean isClass(Type type) {
        return type instanceof Class;
    }

    /**
     * judge a type is a ParameterizedType
     */
    public static boolean isParameterizedType(Type type) {
        return (type instanceof ParameterizedType);
    }

    /**
     * get the wrap class for a primitive type
     */
    public static Class<?> getPrimitiveWrapClass(Type type) {
        return Primitives.wrap(type);
    }


    /**
     * get the type's signature in jvm runtime
     *
     * @param typeString type FQN
     * @return signature
     */
    public static String getTypeSignature(@NonNull String typeString) {
        return TypeSignatures.toTypeSignature(typeString);
    }


    /**
     * Returns an array type whose elements are all instances of
     * {@code componentType}.
     *
     * @return a {@link java.io.Serializable serializable} generic array type.
     */
    public static GenericArrayType arrayOf(Type componentType) {
        return new GsonParameterizedTypeImpl.GenericArrayTypeImpl(componentType);
    }


    public static Class<?> getRawType(Type type) {
        if (type instanceof Class<?>) {
            // type is a normal class.
            return (Class<?>) type;

        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            // I'm not exactly sure why getRawType() returns Type instead of Class.
            // Neal isn't either but suspects some pathological case related
            // to nested classes exists.
            Type rawType = parameterizedType.getRawType();
            Assert.isTrue(rawType instanceof Class);
            return (Class<?>) rawType;

        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();

        } else if (type instanceof TypeVariable) {
            // we could use the variable's bounds, but that won't work if there are multiple.
            // having a raw type that's more general than necessary is okay
            return Object.class;

        } else if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);

        } else {
            String className = type == null ? "null" : type.getClass().getName();
            throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                    + "GenericArrayType, but <" + type + "> is of type " + className);
        }
    }

    /**
     * convert any type to Class
     */
    public static Class<?> toClass(Type o) {
        if (o instanceof GenericArrayType) {
            return Array.newInstance(toClass(((GenericArrayType) o).getGenericComponentType()),
                            0)
                    .getClass();
        }
        if (isPrimitive(o)) {
            return getPrimitiveWrapClass(o);
        }
        if (isClass(o)) {
            return (Class<?>) o;
        }
        if (isParameterizedType(o)) {
            ParameterizedType type = (ParameterizedType) o;
            return getParameterizedTypeWithOwnerType(type.getOwnerType(), type.getRawType(), type.getActualTypeArguments()).getClass();
        }
        throw new IllegalArgumentException();
    }

    /**
     * <pre>
     *     List<E>
     * </pre>
     */
    public static ParameterizedType getSetParameterizedType(Type elementType) {
        return getParameterizedType(Set.class, elementType);
    }

    /**
     * <pre>
     *     List<E>
     * </pre>
     */
    public static ParameterizedType getListParameterizedType(Type elementType) {
        return getParameterizedType(List.class, elementType);
    }

    /**
     * <pre>
     *     Map<K,V>
     * </pre>
     */
    public static ParameterizedType getMapParameterizedType(Type keyType, Type valueType) {
        return getParameterizedType(Map.class, keyType, valueType);
    }

    public static ParameterizedType getParameterizedType(Type rawType) {
        return new GsonParameterizedTypeImpl(null, rawType);
    }

    /**
     * @see #getListParameterizedType(Type)
     * @see #getMapParameterizedType(Type, Type)
     */
    public static ParameterizedType getParameterizedType(Type rawType, Type... typeArguments) {
        return getParameterizedTypeWithOwnerType(null, rawType, typeArguments);
    }

    public static ParameterizedType getParameterizedTypeWithOwnerType(Type ownerType, Type rawType, Type... typeArguments) {
        return new GsonParameterizedTypeImpl(ownerType, rawType, typeArguments);
    }

    public static Type checkPrimitiveArray(GenericArrayType genericArrayType) {
        Type clz = genericArrayType;
        Type genericComponentType = genericArrayType.getGenericComponentType();

        StringBuilder prefix = new StringBuilder("[");
        while (genericComponentType instanceof GenericArrayType) {
            genericComponentType = ((GenericArrayType) genericComponentType).getGenericComponentType();
            prefix.append("[");
        }

        if (genericComponentType instanceof Class<?>) {
            Class<?> ck = (Class<?>) genericComponentType;
            if (ck.isPrimitive()) {
                try {
                    if (ck == boolean.class) {
                        clz = Class.forName(prefix + "Z");
                    } else if (ck == char.class) {
                        clz = Class.forName(prefix + "C");
                    } else if (ck == byte.class) {
                        clz = Class.forName(prefix + "B");
                    } else if (ck == short.class) {
                        clz = Class.forName(prefix + "S");
                    } else if (ck == int.class) {
                        clz = Class.forName(prefix + "I");
                    } else if (ck == long.class) {
                        clz = Class.forName(prefix + "J");
                    } else if (ck == float.class) {
                        clz = Class.forName(prefix + "F");
                    } else if (ck == double.class) {
                        clz = Class.forName(prefix + "D");
                    }
                } catch (ClassNotFoundException e) {
                    // ignore it
                }
            }
        }

        return clz;
    }

    /**
     * @param toResolve 将要被解析的类型
     */
    public static Type resolve(Type context, Class<?> contextRawType, Type toResolve) {
        // This implementation is made a little more complicated in an attempt to avoid object-creation.
        while (true) {
            if (toResolve instanceof TypeVariable) {
                TypeVariable<?> typeVariable = (TypeVariable<?>) toResolve;
                toResolve = resolveTypeVariable(context, contextRawType, typeVariable);
                if (toResolve == typeVariable) {
                    return toResolve;
                }

            } else if (toResolve instanceof Class && ((Class<?>) toResolve).isArray()) {
                Class<?> original = (Class<?>) toResolve;
                Type componentType = original.getComponentType();
                Type newComponentType = resolve(context, contextRawType, componentType);
                return componentType == newComponentType
                        ? original
                        : arrayOf(newComponentType);

            } else if (toResolve instanceof GenericArrayType) {
                GenericArrayType original = (GenericArrayType) toResolve;
                Type componentType = original.getGenericComponentType();
                Type newComponentType = resolve(context, contextRawType, componentType);
                return componentType == newComponentType
                        ? original
                        : arrayOf(newComponentType);

            } else if (toResolve instanceof ParameterizedType) {
                ParameterizedType original = (ParameterizedType) toResolve;
                Type ownerType = original.getOwnerType();
                Type newOwnerType = resolve(context, contextRawType, ownerType);
                boolean changed = newOwnerType != ownerType;

                Type[] args = original.getActualTypeArguments();
                for (int t = 0, length = args.length; t < length; t++) {
                    Type resolvedTypeArgument = resolve(context, contextRawType, args[t]);
                    if (resolvedTypeArgument != args[t]) {
                        if (!changed) {
                            args = args.clone();
                            changed = true;
                        }
                        args[t] = resolvedTypeArgument;
                    }
                }

                return changed
                        ? new GsonParameterizedTypeImpl(newOwnerType, original.getRawType(), args)
                        : original;

            } else if (toResolve instanceof WildcardType) {
                WildcardType original = (WildcardType) toResolve;
                Type[] originalLowerBound = original.getLowerBounds();
                Type[] originalUpperBound = original.getUpperBounds();

                if (originalLowerBound.length == 1) {
                    Type lowerBound = resolve(context, contextRawType, originalLowerBound[0]);
                    if (lowerBound != originalLowerBound[0]) {
                        return supertypeOf(lowerBound);
                    }
                } else if (originalUpperBound.length == 1) {
                    Type upperBound = resolve(context, contextRawType, originalUpperBound[0]);
                    if (upperBound != originalUpperBound[0]) {
                        return subtypeOf(upperBound);
                    }
                }
                return original;

            } else {
                return toResolve;
            }
        }
    }

    /**
     * Returns the declaring class of {@code typeVariable}, or {@code null} if it was not declared by
     * a class.
     */
    private static Class<?> declaringClassOf(TypeVariable<?> typeVariable) {
        GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
        return genericDeclaration instanceof Class ? (Class<?>) genericDeclaration : null;
    }

    private static int indexOf(Object[] array, Object toFind) {
        for (int i = 0; i < array.length; i++) {
            if (toFind.equals(array[i])) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }

    private static Type resolveTypeVariable(Type context, Class<?> contextRawType, TypeVariable<?> unknown) {
        Class<?> declaredByRaw = declaringClassOf(unknown);

        // We can't reduce this further.
        if (declaredByRaw == null) {
            return unknown;
        }

        Type declaredBy = getGenericSupertype(context, contextRawType, declaredByRaw);
        if (declaredBy instanceof ParameterizedType) {
            int index = indexOf(declaredByRaw.getTypeParameters(), unknown);
            return ((ParameterizedType) declaredBy).getActualTypeArguments()[index];
        }

        return unknown;
    }

    /**
     * Returns the generic supertype for {@code supertype}. For example, given a class {@code
     * IntegerSet}, the result for when supertype is {@code Set.class} is {@code Set<Integer>} and the
     * result when the supertype is {@code Collection.class} is {@code Collection<Integer>}.
     */
    public static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
        if (toResolve == rawType) {
            return context;
        }

        // we skip searching through interfaces if unknown is an interface
        if (toResolve.isInterface()) {
            Class<?>[] interfaces = rawType.getInterfaces();
            for (int i = 0, length = interfaces.length; i < length; i++) {
                if (interfaces[i] == toResolve) {
                    return rawType.getGenericInterfaces()[i];
                } else if (toResolve.isAssignableFrom(interfaces[i])) {
                    return getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], toResolve);
                }
            }
        }

        // check our supertypes
        if (!rawType.isInterface()) {
            while (rawType != Object.class) {
                Class<?> rawSupertype = rawType.getSuperclass();
                if (rawSupertype == toResolve) {
                    return rawType.getGenericSuperclass();
                } else if (toResolve.isAssignableFrom(rawSupertype)) {
                    return getGenericSupertype(rawType.getGenericSuperclass(), rawSupertype, toResolve);
                }
                rawType = rawSupertype;
            }
        }

        // we can't resolve this further
        return toResolve;
    }

    /**
     * Returns a type that represents an unknown type that extends {@code bound}. For example, if
     * {@code bound} is {@code CharSequence.class}, this returns {@code ? extends CharSequence}. If
     * {@code bound} is {@code Object.class}, this returns {@code ?}, which is shorthand for {@code
     * ? extends Object}.
     */
    public static WildcardType subtypeOf(Type bound) {
        return new GsonParameterizedTypeImpl.WildcardTypeImpl(new Type[]{bound}, EMPTY_TYPE_ARRAY);
    }

    /**
     * Returns a type that represents an unknown supertype of {@code bound}. For example, if {@code
     * bound} is {@code String.class}, this returns {@code ? super String}.
     */
    public static WildcardType supertypeOf(Type bound) {
        return new GsonParameterizedTypeImpl.WildcardTypeImpl(new Type[]{Object.class}, new Type[]{bound});
    }

    /**
     * 判断是否为 字面量类型
     */
    public static boolean isLiteralType(Type type) {
        if (Primitives.isPrimitive(type) || Primitives.isWrapperType(type)) {
            return true;
        }
        if (isClass(type)) {
            return type == String.class;
        }
        return false;
    }


    /**
     * Returns the generic form of {@code supertype}. For example, if this is {@code
     * ArrayList<String>}, this returns {@code Iterable<String>} given the input {@code
     * Iterable.class}.
     *
     * @param supertype a superclass of, or interface implemented by, this.
     */
    static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
        if (context instanceof WildcardType) {
            // wildcards are useless for resolving supertypes. As the upper bound has the same raw type, use it instead
            context = ((WildcardType) context).getUpperBounds()[0];
        }
        Valid.isTrue(supertype.isAssignableFrom(contextRawType));
        return resolve(context, contextRawType, getGenericSupertype(context, contextRawType, supertype));
    }


    /**
     * Returns the element type of this collection type.
     *
     * @throws IllegalArgumentException if this type is not a collection.
     */
    public static Type getCollectionElementType(Type context, Class<?> contextRawType) {
        Type collectionType = getSupertype(context, contextRawType, Collection.class);

        if (collectionType instanceof WildcardType) {
            collectionType = ((WildcardType) collectionType).getUpperBounds()[0];
        }
        if (collectionType instanceof ParameterizedType) {
            return ((ParameterizedType) collectionType).getActualTypeArguments()[0];
        }
        return Object.class;
    }

    /**
     * Returns a two element array containing this map's key and value types in
     * positions 0 and 1 respectively.
     */
    public static Type[] getMapKeyAndValueTypes(Type context, Class<?> contextRawType) {
        /*
         * Work around a problem with the declaration of java.util.Properties. That
         * class should extend Hashtable<String, String>, but it's declared to
         * extend Hashtable<Object, Object>.
         */
        if (context == Properties.class) {
            return new Type[]{String.class, String.class};
        }

        Type mapType = getSupertype(context, contextRawType, Map.class);
        if (mapType instanceof ParameterizedType) {
            ParameterizedType mapParameterizedType = (ParameterizedType) mapType;
            return mapParameterizedType.getActualTypeArguments();
        }
        return new Type[]{Object.class, Object.class};
    }

    /**
     * 获取类型的泛型类型
     * <p>
     * 如果有返回数组
     * 如果没有返回 null
     *
     * @param type 类型
     * @return 泛型数组
     */
    public static Class<?>[] getTypeParameterizedTypes(Type type) {
        if (type instanceof ParameterizedType) {
            Type[] arguments = ((ParameterizedType) type).getActualTypeArguments();
            int argumentsLength = arguments.length;
            Class<?>[] classes = new Class[argumentsLength];
            for (int i = 0; i < argumentsLength; i++) {
                Type argument = arguments[i];
                if (argument instanceof ParameterizedType) {
                    classes[i] = ((Class<?>) ((ParameterizedType) argument).getRawType());
                } else if (argument instanceof Class) {
                    classes[i] = (Class<?>) argument;
                } else {
                    classes[i] = Object.class;
                }
            }
            return classes;
        } else {
            return null;
        }
    }


    /**
     * 获取 type 对应的原始 class
     *
     * @param type type
     * @return 原始 class
     */
    public static Class<?> getTypeRawClass(Type type) {
        if (type != null) {
            if (type instanceof Class) {
                return (Class<?>) type;
            } else if (type instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) type).getRawType();
            } else if (type instanceof TypeVariable) {
                return (Class<?>) ((TypeVariable<?>) type).getBounds()[0];
            } else if (type instanceof WildcardType) {
                final Type[] upperBounds = ((WildcardType) type).getUpperBounds();
                if (upperBounds.length == 1) {
                    return getTypeRawClass(upperBounds[0]);
                }
            }
        }
        return null;
    }


    /**
     * 获取实际泛型类型
     *
     * @param actualType      实际类型 ArrayList
     * @param typeDefineClass 父类类型 List
     * @param typeVariables   参数类型 String
     * @return 泛型类型
     */
    public static Type[] getActualTypes(Type actualType, Class<?> typeDefineClass, Type... typeVariables) {
        if (!typeDefineClass.isAssignableFrom(getTypeRawClass(actualType))) {
            throw new IllegalArgumentException(StringUtils.format("parameter {} must be assignable from {}", typeDefineClass, actualType));
        }
        TypeVariable<?>[] typeVars = typeDefineClass.getTypeParameters();
        if (ArrayUtils.isEmpty(typeVars)) {
            return null;
        }
        Type[] actualTypeArguments = getTypeArguments(actualType);
        if (ArrayUtils.isEmpty(actualTypeArguments)) {
            return null;
        }
        int size = Math.min(typeVars.length, actualTypeArguments.length);
        Map<Type, Type> tableMap = Maps.of(typeVars, actualTypeArguments);
        Type[] result = new Type[size];
        for (int i = 0; i < typeVariables.length; i++) {
            result[i] = typeVariables[i] instanceof TypeVariable ? tableMap.get(typeVariables[i]) : typeVariables[i];
        }
        return result;
    }


    /**
     * 获得Type对应的原始类
     *
     * @param type {@link Type}
     * @return 原始类，如果无法获取原始类，返回{@code null}
     */
    public static Class<?> getClass(Type type) {
        if (null != type) {
            if (type instanceof Class) {
                return (Class<?>) type;
            } else if (type instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) type).getRawType();
            } else if (type instanceof TypeVariable) {
                return (Class<?>) ((TypeVariable<?>) type).getBounds()[0];
            } else if (type instanceof WildcardType) {
                final Type[] upperBounds = ((WildcardType) type).getUpperBounds();
                if (upperBounds.length == 1) {
                    return getClass(upperBounds[0]);
                }
            }
        }
        return null;
    }

    /**
     * 获取字段对应的Type类型<br>
     * 方法优先获取GenericType，获取不到则获取Type
     *
     * @param field 字段
     * @return {@link Type}，可能为{@code null}
     */
    public static Type getType(Field field) {
        if (null == field) {
            return null;
        }
        return field.getGenericType();
    }

    /**
     * 获得字段的泛型类型
     *
     * @param clazz     Bean类
     * @param fieldName 字段名
     * @return 字段的泛型类型
     */
    public static Type getFieldType(Class<?> clazz, String fieldName) {
        return getType(ClassUtils.getField(clazz, fieldName));
    }

    /**
     * 获得Field对应的原始类
     *
     * @param field {@link Field}
     * @return 原始类，如果无法获取原始类，返回{@code null}
     */
    public static Class<?> getClass(Field field) {
        return null == field ? null : field.getType();
    }

    // -----------------------------------------------------------------------------------
    // Param Type

    /**
     * 获取方法的第一个参数类型<br>
     * 优先获取方法的GenericParameterTypes，如果获取不到，则获取ParameterTypes
     *
     * @param method 方法
     * @return {@link Type}，可能为{@code null}
     */
    public static Type getFirstParamType(Method method) {
        return getParamType(method, 0);
    }

    /**
     * 获取方法的第一个参数类
     *
     * @param method 方法
     * @return 第一个参数类型，可能为{@code null}
     */
    public static Class<?> getFirstParamClass(Method method) {
        return getParamClass(method, 0);
    }

    /**
     * 获取方法的参数类型<br>
     * 优先获取方法的GenericParameterTypes，如果获取不到，则获取ParameterTypes
     *
     * @param method 方法
     * @param index  第几个参数的索引，从0开始计数
     * @return {@link Type}，可能为{@code null}
     */
    public static Type getParamType(Method method, int index) {
        Type[] types = getParamTypes(method);
        if (null != types && types.length > index) {
            return types[index];
        }
        return null;
    }

    /**
     * 获取方法的参数类
     *
     * @param method 方法
     * @param index  第几个参数的索引，从0开始计数
     * @return 参数类，可能为{@code null}
     */
    public static Class<?> getParamClass(Method method, int index) {
        Class<?>[] classes = getParamClasses(method);
        if (null != classes && classes.length > index) {
            return classes[index];
        }
        return null;
    }

    /**
     * 获取方法的参数类型列表<br>
     * 优先获取方法的GenericParameterTypes，如果获取不到，则获取ParameterTypes
     *
     * @param method 方法
     * @return {@link Type}列表，可能为{@code null}
     * @see Method#getGenericParameterTypes()
     * @see Method#getParameterTypes()
     */
    public static Type[] getParamTypes(Method method) {
        return null == method ? null : method.getGenericParameterTypes();
    }

    /**
     * 解析方法的参数类型列表<br>
     * 依赖jre\lib\rt.jar
     *
     * @param method t方法
     * @return 参数类型类列表
     * @see Method#getGenericParameterTypes
     * @see Method#getParameterTypes
     */
    public static Class<?>[] getParamClasses(Method method) {
        return null == method ? null : method.getParameterTypes();
    }

    // -----------------------------------------------------------------------------------
    // Return Type

    /**
     * 获取方法的返回值类型<br>
     * 获取方法的GenericReturnType
     *
     * @param method 方法
     * @return {@link Type}，可能为{@code null}
     * @see Method#getGenericReturnType()
     * @see Method#getReturnType()
     */
    public static Type getReturnType(Method method) {
        return null == method ? null : method.getGenericReturnType();
    }

    /**
     * 解析方法的返回类型类列表
     *
     * @param method 方法
     * @return 返回值类型的类
     * @see Method#getGenericReturnType
     * @see Method#getReturnType
     */
    public static Class<?> getReturnClass(Method method) {
        return null == method ? null : method.getReturnType();
    }

    // -----------------------------------------------------------------------------------
    // Type Argument

    /**
     * 获得给定类的第一个泛型参数
     *
     * @param type 被检查的类型，必须是已经确定泛型类型的类型
     * @return {@link Type}，可能为{@code null}
     */
    public static Type getTypeArgument(Type type) {
        return getTypeArgument(type, 0);
    }

    /**
     * 获得给定类的泛型参数
     *
     * @param type  被检查的类型，必须是已经确定泛型类型的类
     * @param index 泛型类型的索引号，即第几个泛型类型
     * @return {@link Type}
     */
    public static Type getTypeArgument(Type type, int index) {
        final Type[] typeArguments = getTypeArguments(type);
        if (null != typeArguments && typeArguments.length > index) {
            return typeArguments[index];
        }
        return null;
    }

    /**
     * 获得指定类型中所有泛型参数类型，例如：
     *
     * <pre>
     * class A&lt;T&gt;
     * class B extends A&lt;String&gt;
     * </pre>
     * <p>
     * 通过此方法，传入B.class即可得到String
     *
     * @param type 指定类型
     * @return 所有泛型参数类型
     */
    public static Type[] getTypeArguments(Type type) {
        if (null == type) {
            return null;
        }

        final ParameterizedType parameterizedType = toParameterizedType(type);
        return (null == parameterizedType) ? null : parameterizedType.getActualTypeArguments();
    }

    /**
     * 将{@link Type} 转换为{@link ParameterizedType}<br>
     * {@link ParameterizedType}用于获取当前类或父类中泛型参数化后的类型<br>
     * 一般用于获取泛型参数具体的参数类型，例如：
     *
     * <pre>
     * class A&lt;T&gt;
     * class B extends A&lt;String&gt;
     * </pre>
     * <p>
     * 通过此方法，传入B.class即可得到B{@link ParameterizedType}，从而获取到String
     *
     * @param type {@link Type}
     * @return {@link ParameterizedType}
     */
    public static ParameterizedType toParameterizedType(Type type) {
        ParameterizedType result = null;
        if (type instanceof ParameterizedType) {
            result = (ParameterizedType) type;
        } else if (type instanceof Class) {
            final Class<?> clazz = (Class<?>) type;
            Type genericSuper = clazz.getGenericSuperclass();
            if (null == genericSuper || Object.class.equals(genericSuper)) {
                // 如果类没有父类，而是实现一些定义好的泛型接口，则取接口的Type
                final Type[] genericInterfaces = clazz.getGenericInterfaces();
                if (ArrayUtils.isNotEmpty(genericInterfaces)) {
                    // 默认取第一个实现接口的泛型Type
                    genericSuper = genericInterfaces[0];
                }
            }
            result = toParameterizedType(genericSuper);
        }
        return result;
    }

    /**
     * 是否未知类型<br>
     * type为null或者{@link TypeVariable} 都视为未知类型
     *
     * @param type Type类型
     * @return 是否未知类型
     */
    public static boolean isUnknown(Type type) {
        return null == type || type instanceof TypeVariable;
    }

    /**
     * 指定泛型数组中是否含有泛型变量
     *
     * @param types 泛型数组
     * @return 是否含有泛型变量
     */
    public static boolean hasTypeVariable(Type... types) {
        for (Type type : types) {
            if (type instanceof TypeVariable) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取泛型变量和泛型实际类型的对应关系Map，例如：
     *
     * <pre>
     *     T    com.sondertara.test.User
     *     E    java.lang.Integer
     * </pre>
     *
     * @param clazz 被解析的包含泛型参数的类
     * @return 泛型对应关系Map
     */
    public static Map<Type, Type> getTypeMap(Class<?> clazz) {
        return ActualTypeMapperPool.get(clazz);
    }

    /**
     * 获得泛型字段对应的泛型实际类型，如果此变量没有对应的实际类型，返回null
     *
     * @param type  实际类型明确的类
     * @param field 字段
     * @return 实际类型，可能为Class等
     */
    public static Type getActualType(Type type, Field field) {
        if (null == field) {
            return null;
        }
        return getActualType(ObjectUtils.defaultIfNull(type, field.getDeclaringClass()), field.getGenericType());
    }

    /**
     * 获得泛型变量对应的泛型实际类型，如果此变量没有对应的实际类型，返回null
     * 此方法可以处理：
     *
     * <pre>
     *     1. 泛型化对象，类似于Map&lt;User, Key&lt;Long&gt;&gt;
     *     2. 泛型变量，类似于T
     * </pre>
     *
     * @param type         类
     * @param typeVariable 泛型变量，例如T等
     * @return 实际类型，可能为Class等
     */
    public static Type getActualType(Type type, Type typeVariable) {
        if (typeVariable instanceof ParameterizedType) {
            return getActualType(type, (ParameterizedType) typeVariable);
        }

        if (typeVariable instanceof TypeVariable) {
            return ActualTypeMapperPool.getActualType(type, (TypeVariable<?>) typeVariable);
        }

        // 没有需要替换的泛型变量，原样输出
        return typeVariable;
    }

    /**
     * 获得泛型变量对应的泛型实际类型，如果此变量没有对应的实际类型，返回null
     * 此方法可以处理复杂的泛型化对象，类似于Map&lt;User, Key&lt;Long&gt;&gt;
     *
     * @param type              类
     * @param parameterizedType 泛型变量，例如List&lt;T&gt;等
     * @return 实际类型，可能为Class等
     */
    public static Type getActualType(Type type, ParameterizedType parameterizedType) {
        // 字段类型为泛型参数类型，解析对应泛型类型为真实类型，类似于List<T> a
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

        // 泛型对象中含有未被转换的泛型变量
        if (TypeUtils.hasTypeVariable(actualTypeArguments)) {
            actualTypeArguments = getActualTypes(type, parameterizedType.getActualTypeArguments());
            if (ArrayUtils.isNotEmpty(actualTypeArguments)) {
                // 替换泛型变量为实际类型，例如List<T>变为List<String>
                parameterizedType = new com.sondertara.common.reflect.type.ParameterizedTypeImpl(actualTypeArguments, parameterizedType.getOwnerType(),
                        parameterizedType.getRawType());
            }
        }

        return parameterizedType;
    }

    /**
     * 获得泛型变量对应的泛型实际类型，如果此变量没有对应的实际类型，返回null
     *
     * @param type          类
     * @param typeVariables 泛型变量数组，例如T等
     * @return 实际类型数组，可能为Class等
     */
    public static Type[] getActualTypes(Type type, Type... typeVariables) {
        return ActualTypeMapperPool.getActualTypes(type, typeVariables);
    }


    // -------------------- field --------------------

    /**
     * 获取字段泛型
     *
     * @param field        field
     * @param genericIndex generic type genericIndex
     * @return 泛型类型 没有则返回 null
     */
    public static Class<?> getFieldGenericType(Field field, int genericIndex) {
        Class<?>[] types = getTypeParameterizedTypes(field.getGenericType());
        if (types == null) {
            return null;
        }
        return Valid.validIndex(types, genericIndex);
    }

    /**
     * 获取字段泛型列表
     *
     * @param field field
     * @return 泛型类型列表 没有则返回 null
     */
    public static Class<?>[] getFieldGenericTypes(Field field) {
        return getTypeParameterizedTypes(field.getGenericType());
    }

    // -------------------- method parameter --------------------

    /**
     * 获取方法参数泛型
     *
     * @param method         method
     * @param parameterIndex parameterIndex
     * @param genericIndex   genericIndex
     * @return 泛型类型 没有则返回 null
     */
    public static Class<?> getMethodParameterGenericType(Method method, int parameterIndex, int genericIndex) {
        Class<?>[] types = getTypeParameterizedTypes(method.getGenericParameterTypes()[parameterIndex]);
        if (types == null) {
            return null;
        }
        return Valid.validIndex(types, genericIndex);
    }

    /**
     * 获取方法参数泛型列表
     *
     * @param method         method
     * @param parameterIndex index
     * @return 泛型列表 没有则返回 null
     */
    public static Class<?>[] getMethodParameterGenericTypes(Method method, int parameterIndex) {
        return getTypeParameterizedTypes(method.getGenericParameterTypes()[parameterIndex]);
    }

    /**
     * 获取方法参数泛型列表
     *
     * @param method method
     * @return 泛型列表 没有则返回 null
     */
    public static Class<?>[][] getMethodParameterGenericTypes(Method method) {
        return Arrays.stream(method.getGenericParameterTypes())
                .map(TypeUtils::getTypeParameterizedTypes)
                .toArray(Class<?>[][]::new);
    }

    // -------------------- method return --------------------

    /**
     * 获取字段泛型
     *
     * @param method       method
     * @param genericIndex genericIndex
     * @return 泛型类型 没有则返回 null
     */
    public static Class<?> getMethodReturnGenericType(Method method, int genericIndex) {
        Class<?>[] types = getTypeParameterizedTypes(method.getGenericReturnType());
        if (types == null) {
            return null;
        }
        return Valid.validIndex(types, genericIndex);
    }

    /**
     * 获取字段泛型列表
     *
     * @param method method
     * @return 泛型类型列表 没有则返回 null
     */
    public static Class<?>[] getMethodReturnGenericTypes(Method method) {
        return getTypeParameterizedTypes(method.getGenericReturnType());
    }

    // -------------------- class --------------------

    /**
     * 获取类类型泛型
     *
     * @param ref          ref
     * @param genericIndex genericIndex
     * @return 泛型类型 没有则返回 null
     */
    public static Class<?> getClassGenericType(TypeReference<?> ref, int genericIndex) {
        Class<?>[] types = getTypeParameterizedTypes(ref.getType());
        if (types == null) {
            return null;
        }
        return Valid.validIndex(types, genericIndex);
    }

    /**
     * 获取类类型泛型列表
     *
     * @param ref ref
     * @return 泛型类型列表 没有则返回 null
     */
    public static Class<?>[] getClassGenericTypes(TypeReference<?> ref) {
        return getTypeParameterizedTypes(ref.getType());
    }

    // -------------------- super class --------------------

    /**
     * 获取父类泛型
     *
     * @param clazz        clazz
     * @param genericIndex genericIndex
     * @return 泛型类型 没有则返回 null
     */
    public static Class<?> getSuperClassGenericType(Class<?> clazz, int genericIndex) {
        Class<?>[] types = getTypeParameterizedTypes(clazz.getGenericSuperclass());
        if (types == null) {
            return null;
        }
        return Valid.validIndex(types, genericIndex);
    }

    /**
     * 获取父类泛型列表
     *
     * @param clazz clazz
     * @return 泛型类型列表 没有则返回 null
     */
    public static Class<?>[] getSuperClassGenericTypes(Class<?> clazz) {
        return getTypeParameterizedTypes(clazz.getGenericSuperclass());
    }

    // -------------------- interface --------------------

    /**
     * 获取接口泛型
     *
     * @param clazz          clazz
     * @param interfaceClass 接口类型
     * @param genericIndex   genericIndex
     * @return 泛型类型 没有则返回 null
     */
    public static Class<?> getInterfaceGenericType(Class<?> clazz, Class<?> interfaceClass, int genericIndex) {
        Class<?>[] types = getInterfaceGenericTypes(clazz, interfaceClass);
        if (types == null) {
            return null;
        }
        return Valid.validIndex(types, genericIndex);
    }

    /**
     * 获取接口泛型列表
     *
     * @param clazz          clazz
     * @param interfaceClass 接口类型
     * @return 泛型类型列表 没有则返回 null
     */
    public static Class<?>[] getInterfaceGenericTypes(Class<?> clazz, Class<?> interfaceClass) {
        for (Type type : clazz.getGenericInterfaces()) {
            if (interfaceClass.equals(type)) {
                // 相等则代表非泛型类型
                break;
            }
            if (type instanceof ParameterizedType) {
                // 实际类型相等
                if (interfaceClass.equals(((ParameterizedType) type).getRawType())) {
                    return getTypeParameterizedTypes(type);
                }
            }
        }
        return null;
    }

    /**
     * 获取接口泛型列表
     *
     * @param clazz clazz
     * @return 泛型类型列表 没有则 value 为 null
     */
    public static Map<Class<?>, Class<?>[]> getInterfaceGenericTypes(Class<?> clazz) {
        Map<Class<?>, Class<?>[]> map = new LinkedHashMap<>();
        for (Type type : clazz.getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                Type rawClass = ((ParameterizedType) type).getRawType();
                map.put((Class<?>) rawClass, getTypeParameterizedTypes(type));
            }
        }
        return map;
    }














    /**
     * GenericArrayType implementation class.
     *
     * @since 3.2
     */
    private static final class GenericArrayTypeImpl implements GenericArrayType {
        private final Type componentType;

        /**
         * Constructor
         *
         * @param componentType of this array type
         */
        private GenericArrayTypeImpl(final Type componentType) {
            this.componentType = componentType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            return obj == this || obj instanceof GenericArrayType && TypeUtils.equals(this, (GenericArrayType) obj);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Type getGenericComponentType() {
            return componentType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            int result = 67 << 4;
            result |= componentType.hashCode();
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return TypeUtils.toString(this);
        }
    }

    /**
     * ParameterizedType implementation class.
     *
     * @since 3.2
     */
    private static final class ParameterizedTypeImpl implements ParameterizedType {
        private final Class<?> raw;
        private final Type useOwner;
        private final Type[] typeArguments;

        /**
         * Constructor
         *
         * @param rawClass      type
         * @param useOwner      owner type to use, if any
         * @param typeArguments formal type arguments
         */
        private ParameterizedTypeImpl(final Class<?> rawClass, final Type useOwner, final Type[] typeArguments) {
            this.raw = rawClass;
            this.useOwner = useOwner;
            this.typeArguments = Arrays.copyOf(typeArguments, typeArguments.length, Type[].class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            return obj == this || obj instanceof ParameterizedType && TypeUtils.equals(this, (ParameterizedType) obj);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Type[] getActualTypeArguments() {
            return typeArguments.clone();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Type getOwnerType() {
            return useOwner;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Type getRawType() {
            return raw;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            int result = 71 << 4;
            result |= raw.hashCode();
            result <<= 4;
            result |= Objects.hashCode(useOwner);
            result <<= 8;
            result |= Arrays.hashCode(typeArguments);
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return TypeUtils.toString(this);
        }
    }

    /**
     * {@link WildcardType} builder.
     *
     * @since 3.2
     */
    public static class WildcardTypeBuilder implements Builder<WildcardType> {
        private Type[] upperBounds;

        private Type[] lowerBounds;

        /**
         * Constructor
         */
        private WildcardTypeBuilder() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public WildcardType build() {
            return new WildcardTypeImpl(upperBounds, lowerBounds);
        }

        /**
         * Specify lower bounds of the wildcard type to build.
         *
         * @param bounds to set
         * @return {@code this}
         */
        public WildcardTypeBuilder withLowerBounds(final Type... bounds) {
            this.lowerBounds = bounds;
            return this;
        }

        /**
         * Specify upper bounds of the wildcard type to build.
         *
         * @param bounds to set
         * @return {@code this}
         */
        public WildcardTypeBuilder withUpperBounds(final Type... bounds) {
            this.upperBounds = bounds;
            return this;
        }
    }

    /**
     * WildcardType implementation class.
     *
     * @since 3.2
     */
    private static final class WildcardTypeImpl implements WildcardType {
        private final Type[] upperBounds;
        private final Type[] lowerBounds;

        /**
         * Constructor
         *
         * @param upperBounds of this type
         * @param lowerBounds of this type
         */
        private WildcardTypeImpl(final Type[] upperBounds, final Type[] lowerBounds) {
            this.upperBounds = ObjectUtils.defaultIfNull(upperBounds, ArrayUtils.EMPTY_TYPE_ARRAY);
            this.lowerBounds = ObjectUtils.defaultIfNull(lowerBounds, ArrayUtils.EMPTY_TYPE_ARRAY);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            return obj == this || obj instanceof WildcardType && TypeUtils.equals(this, (WildcardType) obj);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Type[] getLowerBounds() {
            return lowerBounds.clone();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Type[] getUpperBounds() {
            return upperBounds.clone();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            int result = 73 << 8;
            result |= Arrays.hashCode(upperBounds);
            result <<= 8;
            result |= Arrays.hashCode(lowerBounds);
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return TypeUtils.toString(this);
        }
    }

    /**
     * A wildcard instance matching {@code ?}.
     *
     * @since 3.2
     */
    public static final WildcardType WILDCARD_ALL = wildcardType().withUpperBounds(Object.class).build();

    /**
     * Appends {@code types} to {@code builder} with separator {@code sep}.
     *
     * @param builder destination
     * @param sep     separator
     * @param types   to append
     * @return {@code builder}
     * @since 3.2
     */
    @SafeVarargs
    private static <T> StringBuilder appendAllTo(final StringBuilder builder, final String sep,
                                                 final T... types) {
        Assert.notEmpty(Assert.noNullElements(types));
        if (types.length > 0) {
            builder.append(toString(types[0]));
            for (int i = 1; i < types.length; i++) {
                builder.append(sep).append(toString(types[i]));
            }
        }
        return builder;
    }

    private static void appendRecursiveTypes(final StringBuilder builder, final int[] recursiveTypeIndexes,
                                             final Type[] argumentTypes) {
        for (int i = 0; i < recursiveTypeIndexes.length; i++) {
            appendAllTo(builder.append('<'), ", ", argumentTypes[i].toString()).append('>');
        }

        final Type[] argumentsFiltered = ArrayUtils.removeAll(argumentTypes, recursiveTypeIndexes);

        if (argumentsFiltered.length > 0) {
            appendAllTo(builder.append('<'), ", ", argumentsFiltered).append('>');
        }
    }

    /**
     * Formats a {@link Class} as a {@link String}.
     *
     * @param cls {@link Class} to format
     * @return String
     * @since 3.2
     */
    private static String classToString(final Class<?> cls) {
        if (cls.isArray()) {
            return toString(cls.getComponentType()) + "[]";
        }

        final StringBuilder buf = new StringBuilder();

        if (cls.getEnclosingClass() != null) {
            buf.append(classToString(cls.getEnclosingClass())).append('.').append(cls.getSimpleName());
        } else {
            buf.append(cls.getName());
        }
        if (cls.getTypeParameters().length > 0) {
            buf.append('<');
            appendAllTo(buf, ", ", cls.getTypeParameters());
            buf.append('>');
        }
        return buf.toString();
    }

    /**
     * Tests, recursively, whether any of the type parameters associated with {@code type} are bound to variables.
     *
     * @param type the type to check for type variables
     * @return boolean
     * @since 3.2
     */
    public static boolean containsTypeVariables(final Type type) {
        if (type instanceof TypeVariable<?>) {
            return true;
        }
        if (type instanceof Class<?>) {
            return ((Class<?>) type).getTypeParameters().length > 0;
        }
        if (type instanceof ParameterizedType) {
            for (final Type arg : ((ParameterizedType) type).getActualTypeArguments()) {
                if (containsTypeVariables(arg)) {
                    return true;
                }
            }
            return false;
        }
        if (type instanceof WildcardType) {
            final WildcardType wild = (WildcardType) type;
            return containsTypeVariables(getImplicitLowerBounds(wild)[0])
                    || containsTypeVariables(getImplicitUpperBounds(wild)[0]);
        }
        if (type instanceof GenericArrayType) {
            return containsTypeVariables(((GenericArrayType) type).getGenericComponentType());
        }
        return false;
    }

    private static boolean containsVariableTypeSameParametrizedTypeBound(final TypeVariable<?> typeVariable,
                                                                         final ParameterizedType parameterizedType) {
        return ArrayUtils.contains(typeVariable.getBounds(), parameterizedType);
    }

    /**
     * Tries to determine the type arguments of a class/interface based on a
     * super parameterized type's type arguments. This method is the inverse of
     * {@link #getTypeArguments(Type, Class)} which gets a class/interface's
     * type arguments based on a subtype. It is far more limited in determining
     * the type arguments for the subject class's type variables in that it can
     * only determine those parameters that map from the subject {@link Class}
     * object to the supertype.
     *
     * <p>
     * Example: {@link java.util.TreeSet
     * TreeSet} sets its parameter as the parameter for
     * {@link java.util.NavigableSet NavigableSet}, which in turn sets the
     * parameter of {@link java.util.SortedSet}, which in turn sets the
     * parameter of {@link Set}, which in turn sets the parameter of
     * {@link java.util.Collection}, which in turn sets the parameter of
     * {@link Iterable}. Since {@link TreeSet}'s parameter maps
     * (indirectly) to {@link Iterable}'s parameter, it will be able to
     * determine that based on the super type {@code Iterable<? extends
     * Map<Integer, ? extends Collection<?>>>}, the parameter of
     * {@link TreeSet} is {@code ? extends Map<Integer, ? extends
     * Collection<?>>}.
     * </p>
     *
     * @param cls                    the class whose type parameters are to be determined, not {@code null}
     * @param superParameterizedType the super type from which {@code cls}'s type
     *                               arguments are to be determined, not {@code null}
     * @return a {@link Map} of the type assignments that could be determined
     * for the type variables in each type in the inheritance hierarchy from
     * {@code type} to {@code toClass} inclusive.
     * @throws NullPointerException if either {@code cls} or {@code superParameterizedType} is {@code null}
     */
    public static Map<TypeVariable<?>, Type> determineTypeArguments(final Class<?> cls,
                                                                    final ParameterizedType superParameterizedType) {
        Objects.requireNonNull(cls, "cls");
        Objects.requireNonNull(superParameterizedType, "superParameterizedType");

        final Class<?> superClass = getRawType(superParameterizedType);

        // compatibility check
        if (!isAssignable(cls, superClass)) {
            return null;
        }

        if (cls.equals(superClass)) {
            return getTypeArguments(superParameterizedType, superClass, null);
        }

        // get the next class in the inheritance hierarchy
        final Type midType = getClosestParentType(cls, superClass);

        // can only be a class or a parameterized type
        if (midType instanceof Class<?>) {
            return determineTypeArguments((Class<?>) midType, superParameterizedType);
        }

        final ParameterizedType midParameterizedType = (ParameterizedType) midType;
        final Class<?> midClass = getRawType(midParameterizedType);
        // get the type variables of the mid class that map to the type
        // arguments of the super class
        final Map<TypeVariable<?>, Type> typeVarAssigns = determineTypeArguments(midClass, superParameterizedType);
        // map the arguments of the mid type to the class type variables
        mapTypeVariablesToArguments(cls, midParameterizedType, typeVarAssigns);

        return typeVarAssigns;
    }

    /**
     * Tests whether {@code t} equals {@code a}.
     *
     * @param genericArrayType LHS
     * @param type             RHS
     * @return boolean
     * @since 3.2
     */
    private static boolean equals(final GenericArrayType genericArrayType, final Type type) {
        return type instanceof GenericArrayType
                && equals(genericArrayType.getGenericComponentType(), ((GenericArrayType) type).getGenericComponentType());
    }

    /**
     * Tests whether {@code t} equals {@code p}.
     *
     * @param parameterizedType LHS
     * @param type              RHS
     * @return boolean
     * @since 3.2
     */
    private static boolean equals(final ParameterizedType parameterizedType, final Type type) {
        if (type instanceof ParameterizedType) {
            final ParameterizedType other = (ParameterizedType) type;
            if (equals(parameterizedType.getRawType(), other.getRawType())
                    && equals(parameterizedType.getOwnerType(), other.getOwnerType())) {
                return equals(parameterizedType.getActualTypeArguments(), other.getActualTypeArguments());
            }
        }
        return false;
    }

    /**
     * Tests equality of types.
     *
     * @param type1 the first type
     * @param type2 the second type
     * @return boolean
     * @since 3.2
     */
    public static boolean equals(final Type type1, final Type type2) {
        if (Objects.equals(type1, type2)) {
            return true;
        }
        if (type1 instanceof ParameterizedType) {
            return equals((ParameterizedType) type1, type2);
        }
        if (type1 instanceof GenericArrayType) {
            return equals((GenericArrayType) type1, type2);
        }
        if (type1 instanceof WildcardType) {
            return equals((WildcardType) type1, type2);
        }
        return false;
    }

    /**
     * Tests whether {@code t1} equals {@code t2}.
     *
     * @param type1 LHS
     * @param type2 RHS
     * @return boolean
     * @since 3.2
     */
    private static boolean equals(final Type[] type1, final Type[] type2) {
        if (type1.length == type2.length) {
            for (int i = 0; i < type1.length; i++) {
                if (!equals(type1[i], type2[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Tests whether {@code t} equals {@code w}.
     *
     * @param wildcardType LHS
     * @param type         RHS
     * @return boolean
     * @since 3.2
     */
    private static boolean equals(final WildcardType wildcardType, final Type type) {
        if (type instanceof WildcardType) {
            final WildcardType other = (WildcardType) type;
            return equals(getImplicitLowerBounds(wildcardType), getImplicitLowerBounds(other))
                    && equals(getImplicitUpperBounds(wildcardType), getImplicitUpperBounds(other));
        }
        return false;
    }

    /**
     * Helper method to establish the formal parameters for a parameterized type.
     *
     * @param mappings  map containing the assignments
     * @param variables expected map keys
     * @return array of map values corresponding to specified keys
     */
    private static Type[] extractTypeArgumentsFrom(final Map<TypeVariable<?>, Type> mappings, final TypeVariable<?>[] variables) {
        final Type[] result = new Type[variables.length];
        int index = 0;
        for (final TypeVariable<?> var : variables) {
            Assert.isTrue(mappings.containsKey(var), "missing argument mapping for %s", toString(var));
            result[index++] = mappings.get(var);
        }
        return result;
    }

    private static int[] findRecursiveTypes(final ParameterizedType parameterizedType) {
        final Type[] filteredArgumentTypes = Arrays.copyOf(parameterizedType.getActualTypeArguments(),
                parameterizedType.getActualTypeArguments().length);
        int[] indexesToRemove = {};
        for (int i = 0; i < filteredArgumentTypes.length; i++) {
            if (filteredArgumentTypes[i] instanceof TypeVariable<?> && containsVariableTypeSameParametrizedTypeBound(
                    (TypeVariable<?>) filteredArgumentTypes[i], parameterizedType)) {
                indexesToRemove = (int[]) ArrayUtils.append(indexesToRemove, i);
            }
        }
        return indexesToRemove;
    }

    /**
     * Creates a generic array type instance.
     *
     * @param componentType the type of the elements of the array. For example the component type of {@code boolean[]}
     *                      is {@code boolean}
     * @return {@link GenericArrayType}
     * @since 3.2
     */
    public static GenericArrayType genericArrayType(final Type componentType) {
        return new GenericArrayTypeImpl(Objects.requireNonNull(componentType, "componentType"));
    }

    /**
     * Formats a {@link GenericArrayType} as a {@link String}.
     *
     * @param genericArrayType {@link GenericArrayType} to format
     * @return String
     * @since 3.2
     */
    private static String genericArrayTypeToString(final GenericArrayType genericArrayType) {
        return String.format("%s[]", toString(genericArrayType.getGenericComponentType()));
    }

    /**
     * Gets the array component type of {@code type}.
     *
     * @param type the type to be checked
     * @return component type or null if type is not an array type
     */
    public static Type getArrayComponentType(final Type type) {
        if (type instanceof Class<?>) {
            final Class<?> cls = (Class<?>) type;
            return cls.isArray() ? cls.getComponentType() : null;
        }
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        }
        return null;
    }

    /**
     * Gets the closest parent type to the
     * super class specified by {@code superClass}.
     *
     * @param cls        the class in question
     * @param superClass the super class
     * @return the closes parent type
     */
    private static Type getClosestParentType(final Class<?> cls, final Class<?> superClass) {
        // only look at the interfaces if the super class is also an interface
        if (superClass.isInterface()) {
            // get the generic interfaces of the subject class
            final Type[] interfaceTypes = cls.getGenericInterfaces();
            // will hold the best generic interface match found
            Type genericInterface = null;

            // find the interface closest to the super class
            for (final Type midType : interfaceTypes) {
                final Class<?> midClass;

                if (midType instanceof ParameterizedType) {
                    midClass = getRawType((ParameterizedType) midType);
                } else if (midType instanceof Class<?>) {
                    midClass = (Class<?>) midType;
                } else {
                    throw new IllegalStateException("Unexpected generic"
                            + " interface type found: " + midType);
                }

                // check if this interface is further up the inheritance chain
                // than the previously found match
                if (isAssignable(midClass, superClass)
                        && isAssignable(genericInterface, (Type) midClass)) {
                    genericInterface = midType;
                }
            }

            // found a match?
            if (genericInterface != null) {
                return genericInterface;
            }
        }

        // none of the interfaces were descendants of the target class, so the
        // super class has to be one, instead
        return cls.getGenericSuperclass();
    }

    /**
     * Gets an array containing the sole type of {@link Object} if
     * {@link TypeVariable#getBounds()} returns an empty array. Otherwise, it
     * returns the result of {@link TypeVariable#getBounds()} passed into
     * {@link #normalizeUpperBounds}.
     *
     * @param typeVariable the subject type variable, not {@code null}
     * @return a non-empty array containing the bounds of the type variable.
     * @throws NullPointerException if {@code typeVariable} is {@code null}
     */
    public static Type[] getImplicitBounds(final TypeVariable<?> typeVariable) {
        Objects.requireNonNull(typeVariable, "typeVariable");
        final Type[] bounds = typeVariable.getBounds();

        return bounds.length == 0 ? new Type[]{Object.class} : normalizeUpperBounds(bounds);
    }

    /**
     * Gets an array containing a single value of {@code null} if
     * {@link WildcardType#getLowerBounds()} returns an empty array. Otherwise,
     * it returns the result of {@link WildcardType#getLowerBounds()}.
     *
     * @param wildcardType the subject wildcard type, not {@code null}
     * @return a non-empty array containing the lower bounds of the wildcard
     * type.
     * @throws NullPointerException if {@code wildcardType} is {@code null}
     */
    public static Type[] getImplicitLowerBounds(final WildcardType wildcardType) {
        Objects.requireNonNull(wildcardType, "wildcardType");
        final Type[] bounds = wildcardType.getLowerBounds();

        return bounds.length == 0 ? new Type[]{null} : bounds;
    }

    /**
     * Gets an array containing the sole value of {@link Object} if
     * {@link WildcardType#getUpperBounds()} returns an empty array. Otherwise,
     * it returns the result of {@link WildcardType#getUpperBounds()}
     * passed into {@link #normalizeUpperBounds}.
     *
     * @param wildcardType the subject wildcard type, not {@code null}
     * @return a non-empty array containing the upper bounds of the wildcard
     * type.
     * @throws NullPointerException if {@code wildcardType} is {@code null}
     */
    public static Type[] getImplicitUpperBounds(final WildcardType wildcardType) {
        Objects.requireNonNull(wildcardType, "wildcardType");
        final Type[] bounds = wildcardType.getUpperBounds();

        return bounds.length == 0 ? new Type[]{Object.class} : normalizeUpperBounds(bounds);
    }

    /**
     * Transforms the passed in type to a {@link Class} object. Type-checking method of convenience.
     *
     * @param parameterizedType the type to be converted
     * @return the corresponding {@link Class} object
     * @throws IllegalStateException if the conversion fails
     */
    private static Class<?> getRawType(final ParameterizedType parameterizedType) {
        final Type rawType = parameterizedType.getRawType();

        // check if raw type is a Class object
        // not currently necessary, but since the return type is Type instead of
        // Class, there's enough reason to believe that future versions of Java
        // may return other Type implementations. And type-safety checking is
        // rarely a bad idea.
        if (!(rawType instanceof Class<?>)) {
            throw new IllegalStateException("Wait... What!? Type of rawType: " + rawType);
        }

        return (Class<?>) rawType;
    }

    /**
     * Gets the raw type of a Java type, given its context. Primarily for use
     * with {@link TypeVariable}s and {@link GenericArrayType}s, or when you do
     * not know the runtime type of {@code type}: if you know you have a
     * {@link Class} instance, it is already raw; if you know you have a
     * {@link ParameterizedType}, its raw type is only a method call away.
     *
     * @param type          to resolve
     * @param assigningType type to be resolved against
     * @return the resolved {@link Class} object or {@code null} if
     * the type could not be resolved
     */
    public static Class<?> getRawType(final Type type, final Type assigningType) {
        if (type instanceof Class<?>) {
            // it is raw, no problem
            return (Class<?>) type;
        }

        if (type instanceof ParameterizedType) {
            // simple enough to get the raw type of a ParameterizedType
            return getRawType((ParameterizedType) type);
        }

        if (type instanceof TypeVariable<?>) {
            if (assigningType == null) {
                return null;
            }

            // get the entity declaring this type variable
            final Object genericDeclaration = ((TypeVariable<?>) type).getGenericDeclaration();

            // can't get the raw type of a method- or constructor-declared type
            // variable
            if (!(genericDeclaration instanceof Class<?>)) {
                return null;
            }

            // get the type arguments for the declaring class/interface based
            // on the enclosing type
            final Map<TypeVariable<?>, Type> typeVarAssigns = getTypeArguments(assigningType,
                    (Class<?>) genericDeclaration);

            // enclosingType has to be a subclass (or subinterface) of the
            // declaring type
            if (typeVarAssigns == null) {
                return null;
            }

            // get the argument assigned to this type variable
            final Type typeArgument = typeVarAssigns.get(type);

            if (typeArgument == null) {
                return null;
            }

            // get the argument for this type variable
            return getRawType(typeArgument, assigningType);
        }

        if (type instanceof GenericArrayType) {
            // get raw component type
            final Class<?> rawComponentType = getRawType(((GenericArrayType) type)
                    .getGenericComponentType(), assigningType);

            // create array type from raw component type and return its class
            return rawComponentType != null ? Array.newInstance(rawComponentType, 0).getClass() : null;
        }

        // (hand-waving) this is not the method you're looking for
        if (type instanceof WildcardType) {
            return null;
        }

        throw new IllegalArgumentException("unknown type: " + type);
    }

    /**
     * Gets a map of the type arguments of a class in the context of {@code toClass}.
     *
     * @param cls               the class in question
     * @param toClass           the context class
     * @param subtypeVarAssigns a map with type variables
     * @return the {@link Map} with type arguments
     */
    private static Map<TypeVariable<?>, Type> getTypeArguments(Class<?> cls, final Class<?> toClass,
                                                               final Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        // make sure they're assignable
        if (!isAssignable(cls, toClass)) {
            return null;
        }

        // can't work with primitives
        if (cls.isPrimitive()) {
            // both classes are primitives?
            if (toClass.isPrimitive()) {
                // dealing with widening here. No type arguments to be
                // harvested with these two types.
                return new HashMap<>();
            }

            // work with wrapper the wrapper class instead of the primitive
            cls = ClassUtils.getWrapClass(cls);
        }

        // create a copy of the incoming map, or an empty one if it's null
        final HashMap<TypeVariable<?>, Type> typeVarAssigns = subtypeVarAssigns == null ? new HashMap<>()
                : new HashMap<>(subtypeVarAssigns);

        // has target class been reached?
        if (toClass.equals(cls)) {
            return typeVarAssigns;
        }

        // walk the inheritance hierarchy until the target class is reached
        return getTypeArguments(getClosestParentType(cls, toClass), toClass, typeVarAssigns);
    }

    /**
     * Gets all the type arguments for this parameterized type
     * including owner hierarchy arguments such as
     * {@code Outer<K, V>.Inner<T>.DeepInner<E>} .
     * The arguments are returned in a
     * {@link Map} specifying the argument type for each {@link TypeVariable}.
     *
     * @param type specifies the subject parameterized type from which to
     *             harvest the parameters.
     * @return a {@link Map} of the type arguments to their respective type
     * variables.
     */
    public static Map<TypeVariable<?>, Type> getTypeArguments(final ParameterizedType type) {
        return getTypeArguments(type, getRawType(type), null);
    }

    /**
     * Gets a map of the type arguments of a parameterized type in the context of {@code toClass}.
     *
     * @param parameterizedType the parameterized type
     * @param toClass           the class
     * @param subtypeVarAssigns a map with type variables
     * @return the {@link Map} with type arguments
     */
    private static Map<TypeVariable<?>, Type> getTypeArguments(
            final ParameterizedType parameterizedType, final Class<?> toClass,
            final Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        final Class<?> cls = getRawType(parameterizedType);

        // make sure they're assignable
        if (!isAssignable(cls, toClass)) {
            return null;
        }

        final Type ownerType = parameterizedType.getOwnerType();
        final Map<TypeVariable<?>, Type> typeVarAssigns;

        if (ownerType instanceof ParameterizedType) {
            // get the owner type arguments first
            final ParameterizedType parameterizedOwnerType = (ParameterizedType) ownerType;
            typeVarAssigns = getTypeArguments(parameterizedOwnerType,
                    getRawType(parameterizedOwnerType), subtypeVarAssigns);
        } else {
            // no owner, prep the type variable assignments map
            typeVarAssigns = subtypeVarAssigns == null ? new HashMap<>()
                    : new HashMap<>(subtypeVarAssigns);
        }

        // get the subject parameterized type's arguments
        final Type[] typeArgs = parameterizedType.getActualTypeArguments();
        // and get the corresponding type variables from the raw class
        final TypeVariable<?>[] typeParams = cls.getTypeParameters();

        // map the arguments to their respective type variables
        for (int i = 0; i < typeParams.length; i++) {
            final Type typeArg = typeArgs[i];
            typeVarAssigns.put(
                    typeParams[i],
                    typeVarAssigns.getOrDefault(typeArg, typeArg)
            );
        }

        if (toClass.equals(cls)) {
            // target class has been reached. Done.
            return typeVarAssigns;
        }

        // walk the inheritance hierarchy until the target class is reached
        return getTypeArguments(getClosestParentType(cls, toClass), toClass, typeVarAssigns);
    }

    /**
     * Gets the type arguments of a class/interface based on a subtype. For
     * instance, this method will determine that both of the parameters for the
     * interface {@link Map} are {@link Object} for the subtype
     * {@link java.util.Properties Properties} even though the subtype does not
     * directly implement the {@link Map} interface.
     *
     * <p>
     * This method returns {@code null} if {@code type} is not assignable to
     * {@code toClass}. It returns an empty map if none of the classes or
     * interfaces in its inheritance hierarchy specify any type arguments.
     * </p>
     *
     * <p>
     * A side effect of this method is that it also retrieves the type
     * arguments for the classes and interfaces that are part of the hierarchy
     * between {@code type} and {@code toClass}. So with the above
     * example, this method will also determine that the type arguments for
     * {@link java.util.Hashtable Hashtable} are also both {@link Object}.
     * In cases where the interface specified by {@code toClass} is
     * (indirectly) implemented more than once (e.g. where {@code toClass}
     * specifies the interface {@link java.lang.Iterable Iterable} and
     * {@code type} specifies a parameterized type that implements both
     * {@link java.util.Set Set} and {@link java.util.Collection Collection}),
     * this method will look at the inheritance hierarchy of only one of the
     * implementations/subclasses; the first interface encountered that isn't a
     * subinterface to one of the others in the {@code type} to
     * {@code toClass} hierarchy.
     * </p>
     *
     * @param type    the type from which to determine the type parameters of
     *                {@code toClass}
     * @param toClass the class whose type parameters are to be determined based
     *                on the subtype {@code type}
     * @return a {@link Map} of the type assignments for the type variables in
     * each type in the inheritance hierarchy from {@code type} to
     * {@code toClass} inclusive.
     */
    public static Map<TypeVariable<?>, Type> getTypeArguments(final Type type, final Class<?> toClass) {
        return getTypeArguments(type, toClass, null);
    }

    /**
     * Gets a map of the type arguments of {@code type} in the context of {@code toClass}.
     *
     * @param type              the type in question
     * @param toClass           the class
     * @param subtypeVarAssigns a map with type variables
     * @return the {@link Map} with type arguments
     */
    private static Map<TypeVariable<?>, Type> getTypeArguments(final Type type, final Class<?> toClass,
                                                               final Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        if (type instanceof Class<?>) {
            return getTypeArguments((Class<?>) type, toClass, subtypeVarAssigns);
        }

        if (type instanceof ParameterizedType) {
            return getTypeArguments((ParameterizedType) type, toClass, subtypeVarAssigns);
        }

        if (type instanceof GenericArrayType) {
            return getTypeArguments(((GenericArrayType) type).getGenericComponentType(), toClass
                    .isArray() ? toClass.getComponentType() : toClass, subtypeVarAssigns);
        }

        // since wildcard types are not assignable to classes, should this just
        // return null?
        if (type instanceof WildcardType) {
            for (final Type bound : getImplicitUpperBounds((WildcardType) type)) {
                // find the first bound that is assignable to the target class
                if (isAssignable(bound, toClass)) {
                    return getTypeArguments(bound, toClass, subtypeVarAssigns);
                }
            }

            return null;
        }

        if (type instanceof TypeVariable<?>) {
            for (final Type bound : getImplicitBounds((TypeVariable<?>) type)) {
                // find the first bound that is assignable to the target class
                if (isAssignable(bound, toClass)) {
                    return getTypeArguments(bound, toClass, subtypeVarAssigns);
                }
            }

            return null;
        }
        throw new IllegalStateException("found an unhandled type: " + type);
    }

    /**
     * Tests whether the specified type denotes an array type.
     *
     * @param type the type to be checked
     * @return {@code true} if {@code type} is an array class or a {@link GenericArrayType}.
     */
    public static boolean isArrayType(final Type type) {
        return type instanceof GenericArrayType || type instanceof Class<?> && ((Class<?>) type).isArray();
    }

    /**
     * Tests if the subject type may be implicitly cast to the target class
     * following the Java generics rules.
     *
     * @param type    the subject type to be assigned to the target type
     * @param toClass the target class
     * @return {@code true} if {@code type} is assignable to {@code toClass}.
     */
    private static boolean isAssignable(final Type type, final Class<?> toClass) {
        if (type == null) {
            // consistency with ClassUtils.isAssignable() behavior
            return toClass == null || !toClass.isPrimitive();
        }

        // only a null type can be assigned to null type which
        // would have cause the previous to return true
        if (toClass == null) {
            return false;
        }

        // all types are assignable to themselves
        if (toClass.equals(type)) {
            return true;
        }

        if (type instanceof Class<?>) {
            // just comparing two classes
            return ClassUtils.isAssignable((Class<?>) type, toClass);
        }

        if (type instanceof ParameterizedType) {
            // only have to compare the raw type to the class
            return isAssignable(getRawType((ParameterizedType) type), toClass);
        }

        // *
        if (type instanceof TypeVariable<?>) {
            // if any of the bounds are assignable to the class, then the
            // type is assignable to the class.
            for (final Type bound : ((TypeVariable<?>) type).getBounds()) {
                if (isAssignable(bound, toClass)) {
                    return true;
                }
            }

            return false;
        }

        // the only classes to which a generic array type can be assigned
        // are class Object and array classes
        if (type instanceof GenericArrayType) {
            return toClass.equals(Object.class)
                    || toClass.isArray()
                    && isAssignable(((GenericArrayType) type).getGenericComponentType(), toClass
                    .getComponentType());
        }

        // wildcard types are not assignable to a class (though one would think
        // "? super Object" would be assignable to Object)
        if (type instanceof WildcardType) {
            return false;
        }

        throw new IllegalStateException("found an unhandled type: " + type);
    }

    /**
     * Tests if the subject type may be implicitly cast to the target
     * generic array type following the Java generics rules.
     *
     * @param type               the subject type to be assigned to the target type
     * @param toGenericArrayType the target generic array type
     * @param typeVarAssigns     a map with type variables
     * @return {@code true} if {@code type} is assignable to
     * {@code toGenericArrayType}.
     */
    private static boolean isAssignable(final Type type, final GenericArrayType toGenericArrayType,
                                        final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }

        // only a null type can be assigned to null type which
        // would have cause the previous to return true
        if (toGenericArrayType == null) {
            return false;
        }

        // all types are assignable to themselves
        if (toGenericArrayType.equals(type)) {
            return true;
        }

        final Type toComponentType = toGenericArrayType.getGenericComponentType();

        if (type instanceof Class<?>) {
            final Class<?> cls = (Class<?>) type;

            // compare the component types
            return cls.isArray()
                    && isAssignable(cls.getComponentType(), toComponentType, typeVarAssigns);
        }

        if (type instanceof GenericArrayType) {
            // compare the component types
            return isAssignable(((GenericArrayType) type).getGenericComponentType(),
                    toComponentType, typeVarAssigns);
        }

        if (type instanceof WildcardType) {
            // so long as one of the upper bounds is assignable, it's good
            for (final Type bound : getImplicitUpperBounds((WildcardType) type)) {
                if (isAssignable(bound, toGenericArrayType)) {
                    return true;
                }
            }

            return false;
        }

        if (type instanceof TypeVariable<?>) {
            // probably should remove the following logic and just return false.
            // type variables cannot specify arrays as bounds.
            for (final Type bound : getImplicitBounds((TypeVariable<?>) type)) {
                if (isAssignable(bound, toGenericArrayType)) {
                    return true;
                }
            }

            return false;
        }

        if (type instanceof ParameterizedType) {
            // the raw type of a parameterized type is never an array or
            // generic array, otherwise the declaration would look like this:
            // Collection[]< ? extends String > collection;
            return false;
        }

        throw new IllegalStateException("found an unhandled type: " + type);
    }

    /**
     * Tests if the subject type may be implicitly cast to the target
     * parameterized type following the Java generics rules.
     *
     * @param type                the subject type to be assigned to the target type
     * @param toParameterizedType the target parameterized type
     * @param typeVarAssigns      a map with type variables
     * @return {@code true} if {@code type} is assignable to {@code toType}.
     */
    private static boolean isAssignable(final Type type, final ParameterizedType toParameterizedType,
                                        final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }

        // only a null type can be assigned to null type which
        // would have cause the previous to return true
        if (toParameterizedType == null) {
            return false;
        }

        // cannot cast an array type to a parameterized type.
        if (type instanceof GenericArrayType) {
            return false;
        }

        // all types are assignable to themselves
        if (toParameterizedType.equals(type)) {
            return true;
        }

        // get the target type's raw type
        final Class<?> toClass = getRawType(toParameterizedType);
        // get the subject type's type arguments including owner type arguments
        // and supertype arguments up to and including the target class.
        final Map<TypeVariable<?>, Type> fromTypeVarAssigns = getTypeArguments(type, toClass, null);

        // null means the two types are not compatible
        if (fromTypeVarAssigns == null) {
            return false;
        }

        // compatible types, but there's no type arguments. this is equivalent
        // to comparing Map< ?, ? > to Map, and raw types are always assignable
        // to parameterized types.
        if (fromTypeVarAssigns.isEmpty()) {
            return true;
        }

        // get the target type's type arguments including owner type arguments
        final Map<TypeVariable<?>, Type> toTypeVarAssigns = getTypeArguments(toParameterizedType,
                toClass, typeVarAssigns);

        // now to check each type argument
        for (final TypeVariable<?> var : toTypeVarAssigns.keySet()) {
            final Type toTypeArg = unrollVariableAssignments(var, toTypeVarAssigns);
            final Type fromTypeArg = unrollVariableAssignments(var, fromTypeVarAssigns);

            if (toTypeArg == null && fromTypeArg instanceof Class) {
                continue;
            }

            // parameters must either be absent from the subject type, within
            // the bounds of the wildcard type, or be an exact match to the
            // parameters of the target type.
            if (fromTypeArg != null && toTypeArg != null
                    && !toTypeArg.equals(fromTypeArg)
                    && !(toTypeArg instanceof WildcardType && isAssignable(fromTypeArg, toTypeArg,
                    typeVarAssigns))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests if the subject type may be implicitly cast to the target type
     * following the Java generics rules. If both types are {@link Class}
     * objects, the method returns the result of
     * {@link ClassUtils#isAssignable(Class, Class)}.
     *
     * @param type   the subject type to be assigned to the target type
     * @param toType the target type
     * @return {@code true} if {@code type} is assignable to {@code toType}.
     */
    public static boolean isAssignable(final Type type, final Type toType) {
        return isAssignable(type, toType, null);
    }

    /**
     * Tests if the subject type may be implicitly cast to the target type
     * following the Java generics rules.
     *
     * @param type           the subject type to be assigned to the target type
     * @param toType         the target type
     * @param typeVarAssigns optional map of type variable assignments
     * @return {@code true} if {@code type} is assignable to {@code toType}.
     */
    private static boolean isAssignable(final Type type, final Type toType,
                                        final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (toType == null || toType instanceof Class<?>) {
            return isAssignable(type, (Class<?>) toType);
        }

        if (toType instanceof ParameterizedType) {
            return isAssignable(type, (ParameterizedType) toType, typeVarAssigns);
        }

        if (toType instanceof GenericArrayType) {
            return isAssignable(type, (GenericArrayType) toType, typeVarAssigns);
        }

        if (toType instanceof WildcardType) {
            return isAssignable(type, (WildcardType) toType, typeVarAssigns);
        }

        if (toType instanceof TypeVariable<?>) {
            return isAssignable(type, (TypeVariable<?>) toType, typeVarAssigns);
        }

        throw new IllegalStateException("found an unhandled type: " + toType);
    }

    /**
     * Tests if the subject type may be implicitly cast to the target type
     * variable following the Java generics rules.
     *
     * @param type           the subject type to be assigned to the target type
     * @param toTypeVariable the target type variable
     * @param typeVarAssigns a map with type variables
     * @return {@code true} if {@code type} is assignable to
     * {@code toTypeVariable}.
     */
    private static boolean isAssignable(final Type type, final TypeVariable<?> toTypeVariable,
                                        final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }

        // only a null type can be assigned to null type which
        // would have cause the previous to return true
        if (toTypeVariable == null) {
            return false;
        }

        // all types are assignable to themselves
        if (toTypeVariable.equals(type)) {
            return true;
        }

        if (type instanceof TypeVariable<?>) {
            // a type variable is assignable to another type variable, if
            // and only if the former is the latter, extends the latter, or
            // is otherwise a descendant of the latter.
            final Type[] bounds = getImplicitBounds((TypeVariable<?>) type);

            for (final Type bound : bounds) {
                if (isAssignable(bound, toTypeVariable, typeVarAssigns)) {
                    return true;
                }
            }
        }

        if (type instanceof Class<?> || type instanceof ParameterizedType
                || type instanceof GenericArrayType || type instanceof WildcardType) {
            return false;
        }

        throw new IllegalStateException("found an unhandled type: " + type);
    }

    /**
     * Tests if the subject type may be implicitly cast to the target
     * wildcard type following the Java generics rules.
     *
     * @param type           the subject type to be assigned to the target type
     * @param toWildcardType the target wildcard type
     * @param typeVarAssigns a map with type variables
     * @return {@code true} if {@code type} is assignable to
     * {@code toWildcardType}.
     */
    private static boolean isAssignable(final Type type, final WildcardType toWildcardType,
                                        final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }

        // only a null type can be assigned to null type which
        // would have cause the previous to return true
        if (toWildcardType == null) {
            return false;
        }

        // all types are assignable to themselves
        if (toWildcardType.equals(type)) {
            return true;
        }

        final Type[] toUpperBounds = getImplicitUpperBounds(toWildcardType);
        final Type[] toLowerBounds = getImplicitLowerBounds(toWildcardType);

        if (type instanceof WildcardType) {
            final WildcardType wildcardType = (WildcardType) type;
            final Type[] upperBounds = getImplicitUpperBounds(wildcardType);
            final Type[] lowerBounds = getImplicitLowerBounds(wildcardType);

            for (Type toBound : toUpperBounds) {
                // if there are assignments for unresolved type variables,
                // now's the time to substitute them.
                toBound = substituteTypeVariables(toBound, typeVarAssigns);

                // each upper bound of the subject type has to be assignable to
                // each
                // upper bound of the target type
                for (final Type bound : upperBounds) {
                    if (!isAssignable(bound, toBound, typeVarAssigns)) {
                        return false;
                    }
                }
            }

            for (Type toBound : toLowerBounds) {
                // if there are assignments for unresolved type variables,
                // now's the time to substitute them.
                toBound = substituteTypeVariables(toBound, typeVarAssigns);

                // each lower bound of the target type has to be assignable to
                // each
                // lower bound of the subject type
                for (final Type bound : lowerBounds) {
                    if (!isAssignable(toBound, bound, typeVarAssigns)) {
                        return false;
                    }
                }
            }
            return true;
        }

        for (final Type toBound : toUpperBounds) {
            // if there are assignments for unresolved type variables,
            // now's the time to substitute them.
            if (!isAssignable(type, substituteTypeVariables(toBound, typeVarAssigns),
                    typeVarAssigns)) {
                return false;
            }
        }

        for (final Type toBound : toLowerBounds) {
            // if there are assignments for unresolved type variables,
            // now's the time to substitute them.
            if (!isAssignable(substituteTypeVariables(toBound, typeVarAssigns), type,
                    typeVarAssigns)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests if the given value can be assigned to the target type
     * following the Java generics rules.
     *
     * @param value the value to be checked
     * @param type  the target type
     * @return {@code true} if {@code value} is an instance of {@code type}.
     */
    public static boolean isInstance(final Object value, final Type type) {
        if (type == null) {
            return false;
        }

        return value == null ? !(type instanceof Class<?>) || !((Class<?>) type).isPrimitive()
                : isAssignable(value.getClass(), type, null);
    }

    /**
     * Maps type variables.
     *
     * @param <T>               the generic type of the class in question
     * @param cls               the class in question
     * @param parameterizedType the parameterized type
     * @param typeVarAssigns    the map to be filled
     */
    private static <T> void mapTypeVariablesToArguments(final Class<T> cls,
                                                        final ParameterizedType parameterizedType, final Map<TypeVariable<?>, Type> typeVarAssigns) {
        // capture the type variables from the owner type that have assignments
        final Type ownerType = parameterizedType.getOwnerType();

        if (ownerType instanceof ParameterizedType) {
            // recursion to make sure the owner's owner type gets processed
            mapTypeVariablesToArguments(cls, (ParameterizedType) ownerType, typeVarAssigns);
        }

        // parameterizedType is a generic interface/class (or it's in the owner
        // hierarchy of said interface/class) implemented/extended by the class
        // cls. Find out which type variables of cls are type arguments of
        // parameterizedType:
        final Type[] typeArgs = parameterizedType.getActualTypeArguments();

        // of the cls's type variables that are arguments of parameterizedType,
        // find out which ones can be determined from the super type's arguments
        final TypeVariable<?>[] typeVars = getRawType(parameterizedType).getTypeParameters();

        // use List view of type parameters of cls so the contains() method can be used:
        final List<TypeVariable<Class<T>>> typeVarList = Arrays.asList(cls
                .getTypeParameters());

        for (int i = 0; i < typeArgs.length; i++) {
            final TypeVariable<?> typeVar = typeVars[i];
            final Type typeArg = typeArgs[i];

            // argument of parameterizedType is a type variable of cls
            if (typeVarList.contains(typeArg)
                    // type variable of parameterizedType has an assignment in
                    // the super type.
                    && typeVarAssigns.containsKey(typeVar)) {
                // map the assignment to the cls's type variable
                typeVarAssigns.put((TypeVariable<?>) typeArg, typeVarAssigns.get(typeVar));
            }
        }
    }

    /**
     * Strips out the redundant upper bound types in type
     * variable types and wildcard types (or it would with wildcard types if
     * multiple upper bounds were allowed).
     *
     * <p>
     * Example, with the variable type declaration:
     * </p>
     *
     * <pre>&lt;K extends java.util.Collection&lt;String&gt; &amp;
     * java.util.List&lt;String&gt;&gt;</pre>
     *
     * <p>
     * since {@link List} is a subinterface of {@link Collection},
     * this method will return the bounds as if the declaration had been:
     * </p>
     *
     * <pre>&lt;K extends java.util.List&lt;String&gt;&gt;</pre>
     *
     * @param bounds an array of types representing the upper bounds of either
     *               {@link WildcardType} or {@link TypeVariable}, not {@code null}.
     * @return an array containing the values from {@code bounds} minus the
     * redundant types.
     * @throws NullPointerException if {@code bounds} is {@code null}
     */
    public static Type[] normalizeUpperBounds(final Type[] bounds) {
        Objects.requireNonNull(bounds, "bounds");
        // don't bother if there's only one (or none) type
        if (bounds.length < 2) {
            return bounds;
        }

        final Set<Type> types = new HashSet<>(bounds.length);

        for (final Type type1 : bounds) {
            boolean subtypeFound = false;

            for (final Type type2 : bounds) {
                if (type1 != type2 && isAssignable(type2, type1, null)) {
                    subtypeFound = true;
                    break;
                }
            }

            if (!subtypeFound) {
                types.add(type1);
            }
        }

        return types.toArray(ArrayUtils.EMPTY_TYPE_ARRAY);
    }

    /**
     * Creates a parameterized type instance.
     *
     * @param rawClass        the raw class to create a parameterized type instance for
     * @param typeVariableMap the map used for parameterization
     * @return {@link ParameterizedType}
     * @throws NullPointerException if either {@code rawClass} or {@code typeVariableMap} is {@code null}
     * @since 3.2
     */
    public static final ParameterizedType parameterize(final Class<?> rawClass,
                                                       final Map<TypeVariable<?>, Type> typeVariableMap) {
        Objects.requireNonNull(rawClass, "rawClass");
        Objects.requireNonNull(typeVariableMap, "typeVariableMap");
        return parameterizeWithOwner(null, rawClass,
                extractTypeArgumentsFrom(typeVariableMap, rawClass.getTypeParameters()));
    }

    /**
     * Creates a parameterized type instance.
     *
     * @param rawClass      the raw class to create a parameterized type instance for
     * @param typeArguments the types used for parameterization
     * @return {@link ParameterizedType}
     * @throws NullPointerException if {@code rawClass} is {@code null}
     * @since 3.2
     */
    public static final ParameterizedType parameterize(final Class<?> rawClass, final Type... typeArguments) {
        return parameterizeWithOwner(null, rawClass, typeArguments);
    }

    /**
     * Formats a {@link ParameterizedType} as a {@link String}.
     *
     * @param parameterizedType {@link ParameterizedType} to format
     * @return String
     * @since 3.2
     */
    private static String parameterizedTypeToString(final ParameterizedType parameterizedType) {
        final StringBuilder builder = new StringBuilder();

        final Type useOwner = parameterizedType.getOwnerType();
        final Class<?> raw = (Class<?>) parameterizedType.getRawType();

        if (useOwner == null) {
            builder.append(raw.getName());
        } else {
            if (useOwner instanceof Class<?>) {
                builder.append(((Class<?>) useOwner).getName());
            } else {
                builder.append(useOwner.toString());
            }
            builder.append('.').append(raw.getSimpleName());
        }

        final int[] recursiveTypeIndexes = findRecursiveTypes(parameterizedType);

        if (recursiveTypeIndexes.length > 0) {
            appendRecursiveTypes(builder, recursiveTypeIndexes, parameterizedType.getActualTypeArguments());
        } else {
            appendAllTo(builder.append('<'), ", ", parameterizedType.getActualTypeArguments()).append('>');
        }

        return builder.toString();
    }

    /**
     * Creates a parameterized type instance.
     *
     * @param owner           the owning type
     * @param rawClass        the raw class to create a parameterized type instance for
     * @param typeVariableMap the map used for parameterization
     * @return {@link ParameterizedType}
     * @throws NullPointerException if either {@code rawClass} or {@code typeVariableMap}
     *                              is {@code null}
     * @since 3.2
     */
    public static final ParameterizedType parameterizeWithOwner(final Type owner, final Class<?> rawClass,
                                                                final Map<TypeVariable<?>, Type> typeVariableMap) {
        Objects.requireNonNull(rawClass, "rawClass");
        Objects.requireNonNull(typeVariableMap, "typeVariableMap");
        return parameterizeWithOwner(owner, rawClass,
                extractTypeArgumentsFrom(typeVariableMap, rawClass.getTypeParameters()));
    }

    /**
     * Creates a parameterized type instance.
     *
     * @param owner         the owning type
     * @param rawClass      the raw class to create a parameterized type instance for
     * @param typeArguments the types used for parameterization
     * @return {@link ParameterizedType}
     * @throws NullPointerException if {@code rawClass} is {@code null}
     * @since 3.2
     */
    public static final ParameterizedType parameterizeWithOwner(final Type owner, final Class<?> rawClass,
                                                                final Type... typeArguments) {
        Objects.requireNonNull(rawClass, "rawClass");
        final Type useOwner;
        if (rawClass.getEnclosingClass() == null) {
            Assert.isTrue(owner == null, "no owner allowed for top-level %s", rawClass);
            useOwner = null;
        } else if (owner == null) {
            useOwner = rawClass.getEnclosingClass();
        } else {
            Assert.isTrue(isAssignable(owner, rawClass.getEnclosingClass()),
                    "%s is invalid owner type for parameterized %s", owner, rawClass);
            useOwner = owner;
        }
        Assert.noNullElements(typeArguments, "null type argument at index %s");
        Assert.isTrue(rawClass.getTypeParameters().length == typeArguments.length,
                "invalid number of type parameters specified: expected %d, got %d", rawClass.getTypeParameters().length,
                typeArguments.length);

        return new ParameterizedTypeImpl(rawClass, useOwner, typeArguments);
    }

    /**
     * Finds the mapping for {@code type} in {@code typeVarAssigns}.
     *
     * @param type           the type to be replaced
     * @param typeVarAssigns the map with type variables
     * @return the replaced type
     * @throws IllegalArgumentException if the type cannot be substituted
     */
    private static Type substituteTypeVariables(final Type type, final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type instanceof TypeVariable<?> && typeVarAssigns != null) {
            final Type replacementType = typeVarAssigns.get(type);

            if (replacementType == null) {
                throw new IllegalArgumentException("missing assignment type for type variable "
                        + type);
            }
            return replacementType;
        }
        return type;
    }

    /**
     * Formats a {@link TypeVariable} including its {@link GenericDeclaration}.
     *
     * @param typeVariable the type variable to create a String representation for, not {@code null}
     * @return String
     * @throws NullPointerException if {@code typeVariable} is {@code null}
     * @since 3.2
     */
    public static String toLongString(final TypeVariable<?> typeVariable) {
        Objects.requireNonNull(typeVariable, "typeVariable");
        final StringBuilder buf = new StringBuilder();
        final GenericDeclaration d = typeVariable.getGenericDeclaration();
        if (d instanceof Class<?>) {
            Class<?> c = (Class<?>) d;
            while (true) {
                if (c.getEnclosingClass() == null) {
                    buf.insert(0, c.getName());
                    break;
                }
                buf.insert(0, c.getSimpleName()).insert(0, '.');
                c = c.getEnclosingClass();
            }
        } else if (d instanceof Type) {// not possible as of now
            buf.append(toString((Type) d));
        } else {
            buf.append(d);
        }
        return buf.append(':').append(typeVariableToString(typeVariable)).toString();
    }

    private static <T> String toString(final T object) {
        return object instanceof Type ? toString((Type) object) : object.toString();
    }

    /**
     * Formats a given type as a Java-esque String.
     *
     * @param type the type to create a String representation for, not {@code null}
     * @return String
     * @throws NullPointerException if {@code type} is {@code null}
     * @since 3.2
     */
    public static String toString(final Type type) {
        Objects.requireNonNull(type, "type");
        if (type instanceof Class<?>) {
            return classToString((Class<?>) type);
        }
        if (type instanceof ParameterizedType) {
            return parameterizedTypeToString((ParameterizedType) type);
        }
        if (type instanceof WildcardType) {
            return wildcardTypeToString((WildcardType) type);
        }
        if (type instanceof TypeVariable<?>) {
            return typeVariableToString((TypeVariable<?>) type);
        }
        if (type instanceof GenericArrayType) {
            return genericArrayTypeToString((GenericArrayType) type);
        }
        throw new IllegalArgumentException(ObjectUtils.identityToString(type));
    }

    /**
     * Determines whether or not specified types satisfy the bounds of their
     * mapped type variables. When a type parameter extends another (such as
     * {@code <T, S extends T>}), uses another as a type parameter (such as
     * {@code <T, S extends Comparable>>}), or otherwise depends on
     * another type variable to be specified, the dependencies must be included
     * in {@code typeVarAssigns}.
     *
     * @param typeVariableMap specifies the potential types to be assigned to the
     *                        type variables, not {@code null}.
     * @return whether or not the types can be assigned to their respective type
     * variables.
     * @throws NullPointerException if {@code typeVariableMap} is {@code null}
     */
    public static boolean typesSatisfyVariables(final Map<TypeVariable<?>, Type> typeVariableMap) {
        Objects.requireNonNull(typeVariableMap, "typeVariableMap");
        // all types must be assignable to all the bounds of their mapped
        // type variable.
        for (final Map.Entry<TypeVariable<?>, Type> entry : typeVariableMap.entrySet()) {
            final TypeVariable<?> typeVar = entry.getKey();
            final Type type = entry.getValue();

            for (final Type bound : getImplicitBounds(typeVar)) {
                if (!isAssignable(type, substituteTypeVariables(bound, typeVariableMap),
                        typeVariableMap)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Formats a {@link TypeVariable} as a {@link String}.
     *
     * @param typeVariable {@link TypeVariable} to format
     * @return String
     * @since 3.2
     */
    private static String typeVariableToString(final TypeVariable<?> typeVariable) {
        final StringBuilder buf = new StringBuilder(typeVariable.getName());
        final Type[] bounds = typeVariable.getBounds();
        if (bounds.length > 0 && !(bounds.length == 1 && Object.class.equals(bounds[0]))) {
            buf.append(" extends ");
            appendAllTo(buf, " & ", typeVariable.getBounds());
        }
        return buf.toString();
    }

    /**
     * Unrolls variables in a type bounds array.
     *
     * @param typeArguments assignments {@link Map}
     * @param bounds        in which to expand variables
     * @return {@code bounds} with any variables reassigned
     * @since 3.2
     */
    private static Type[] unrollBounds(final Map<TypeVariable<?>, Type> typeArguments, final Type[] bounds) {
        Type[] result = bounds;
        int i = 0;
        for (; i < result.length; i++) {
            final Type unrolled = unrollVariables(typeArguments, result[i]);
            if (unrolled == null) {
                result = ArrayUtils.remove(result, i--);
            } else {
                result[i] = unrolled;
            }
        }
        return result;
    }

    /**
     * Looks up {@code typeVariable} in {@code typeVarAssigns} <em>transitively</em>, i.e. keep looking until the value
     * found is <em>not</em> a type variable.
     *
     * @param typeVariable   the type variable to look up
     * @param typeVarAssigns the map used for the look-up
     * @return Type or {@code null} if some variable was not in the map
     * @since 3.2
     */
    private static Type unrollVariableAssignments(TypeVariable<?> typeVariable, final Map<TypeVariable<?>, Type> typeVarAssigns) {
        Type result;
        do {
            result = typeVarAssigns.get(typeVariable);
            if (!(result instanceof TypeVariable<?>) || result.equals(typeVariable)) {
                break;
            }
            typeVariable = (TypeVariable<?>) result;
        } while (true);
        return result;
    }

    /**
     * Gets a type representing {@code type} with variable assignments "unrolled."
     *
     * @param typeArguments as from {@link TypeUtils#getTypeArguments(Type, Class)}
     * @param type          the type to unroll variable assignments for
     * @return Type
     * @since 3.2
     */
    public static Type unrollVariables(Map<TypeVariable<?>, Type> typeArguments, final Type type) {
        if (typeArguments == null) {
            typeArguments = Collections.emptyMap();
        }
        if (containsTypeVariables(type)) {
            if (type instanceof TypeVariable<?>) {
                return unrollVariables(typeArguments, typeArguments.get(type));
            }
            if (type instanceof ParameterizedType) {
                final ParameterizedType p = (ParameterizedType) type;
                final Map<TypeVariable<?>, Type> parameterizedTypeArguments;
                if (p.getOwnerType() == null) {
                    parameterizedTypeArguments = typeArguments;
                } else {
                    parameterizedTypeArguments = new HashMap<>(typeArguments);
                    parameterizedTypeArguments.putAll(getTypeArguments(p));
                }
                final Type[] args = p.getActualTypeArguments();
                for (int i = 0; i < args.length; i++) {
                    final Type unrolled = unrollVariables(parameterizedTypeArguments, args[i]);
                    if (unrolled != null) {
                        args[i] = unrolled;
                    }
                }
                return parameterizeWithOwner(p.getOwnerType(), (Class<?>) p.getRawType(), args);
            }
            if (type instanceof WildcardType) {
                final WildcardType wild = (WildcardType) type;
                return wildcardType().withUpperBounds(unrollBounds(typeArguments, wild.getUpperBounds()))
                        .withLowerBounds(unrollBounds(typeArguments, wild.getLowerBounds())).build();
            }
        }
        return type;
    }

    /**
     * Gets a {@link WildcardTypeBuilder}.
     *
     * @return {@link WildcardTypeBuilder}
     * @since 3.2
     */
    public static WildcardTypeBuilder wildcardType() {
        return new WildcardTypeBuilder();
    }

    /**
     * Formats a {@link WildcardType} as a {@link String}.
     *
     * @param wildcardType {@link WildcardType} to format
     * @return String
     * @since 3.2
     */
    private static String wildcardTypeToString(final WildcardType wildcardType) {
        final StringBuilder buf = new StringBuilder().append('?');
        final Type[] lowerBounds = wildcardType.getLowerBounds();
        final Type[] upperBounds = wildcardType.getUpperBounds();
        if (lowerBounds.length > 1 || lowerBounds.length == 1 && lowerBounds[0] != null) {
            appendAllTo(buf.append(" super "), " & ", lowerBounds);
        } else if (upperBounds.length > 1 || upperBounds.length == 1 && !Object.class.equals(upperBounds[0])) {
            appendAllTo(buf.append(" extends "), " & ", upperBounds);
        }
        return buf.toString();
    }

}
