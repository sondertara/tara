package com.sondertara.common.invocation.proxy.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 方法匹配器
 */
public interface MethodMatcher {
    /**
     * 匹配
     *
     * @param signature 方法签名
     * @return 是否匹配
     */
    boolean match(MethodSignature signature);

    /**
     * 匹配所有方法
     */
    static MethodMatcher all() {
        return signature -> true;
    }

    /**
     * 匹配指定名称的方法
     *
     * @param name 方法名
     */
    static MethodMatcher withName(String name) {
        return signature -> name.equals(signature.getName());
    }

    /**
     * 匹配方法名具有特定模式的方法
     *
     * @param regex 正则表达式串
     */
    static MethodMatcher withPattern(String regex) {
        Pattern pattern = Pattern.compile(regex);
        return signature -> pattern.matcher(signature.getName()).matches();
    }

    /**
     * 匹配具有特定返回值的方法
     *
     * @param type 返回值类型
     */
    static MethodMatcher withReturnType(Class<?> type) {
        return signature -> type.equals(signature.getReturnType());
    }

    /**
     * 匹配具有指定参数类型的方法
     *
     * @param types 参数类型数组
     */
    static MethodMatcher withParameterTypes(Class<?>... types) {
        return signature -> Arrays.equals(types, signature.getParameterTypes());
    }

    /**
     * 匹配存在于另一个类型中的方法
     *
     * @param type 类型
     */
    static MethodMatcher existInType(Class<?> type) {
        Method[] methods = type.getDeclaredMethods();
        return signature -> {
            for (Method m : methods) {
                if (m.getName().equals(signature.getName())
                        && m.getReturnType() == signature.getReturnType()
                        && Arrays.equals(m.getParameterTypes(), signature.getParameterTypes())) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * 匹配被指定注解标注的方法
     *
     * @param annotationClass 注解类型
     */
    static <T extends Annotation> MethodMatcher hasAnnotation(Class<T> annotationClass) {
        return signature -> signature.hasAnnotation(annotationClass);
    }

    /**
     * 匹配同时满足两个匹配条件的方法
     *
     * @param matcher 另一个MethodMatcher
     */
    default MethodMatcher and(MethodMatcher matcher) {
        return signature -> this.match(signature) && matcher.match(signature);
    }

    /**
     * 匹配至少满足两个匹配条件其中之一的方法
     *
     * @param matcher 另一个MethodMatcher
     */
    default MethodMatcher or(MethodMatcher matcher) {
        return signature -> this.match(signature) || matcher.match(signature);
    }

    /**
     * 匹配不满足指定匹配结果的方法
     */
    default MethodMatcher not() {
        return signature -> !this.match(signature);
    }

    /**
     * 匹配方法名
     *
     * @param name 方法名
     */
    default MethodMatcher andName(String name) {
        return this.and(withName(name));
    }

    /**
     * 匹配方法名模式
     *
     * @param regex 正则表达式串
     */
    default MethodMatcher andPattern(String regex) {
        return this.and(withPattern(regex));
    }

    /**
     * 匹配方法返回值
     *
     * @param type 返回值类型
     */
    default MethodMatcher andReturnType(Class<?> type) {
        return this.and(withReturnType(type));
    }

    /**
     * 匹配方法参数类型
     *
     * @param types 参数类型数组
     */
    default MethodMatcher andParameterTypes(Class<?>... types) {
        return this.and(withParameterTypes(types));
    }
}
