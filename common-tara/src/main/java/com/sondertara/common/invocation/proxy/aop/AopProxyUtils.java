package com.sondertara.common.invocation.proxy.aop;


import com.sondertara.common.invocation.proxy.ProxyType;
import com.sondertara.common.invocation.proxy.ProxyUtils;
import com.sondertara.common.invocation.proxy.aop.annotation.After;
import com.sondertara.common.invocation.proxy.aop.annotation.AfterThrowing;
import com.sondertara.common.invocation.proxy.aop.annotation.Around;
import com.sondertara.common.invocation.proxy.aop.annotation.Before;
import com.sondertara.common.invocation.proxy.aop.annotation.Filter;
import com.sondertara.common.invocation.proxy.aop.annotation.Order;
import com.sondertara.common.invocation.proxy.aop.annotation.Replace;
import com.sondertara.common.invocation.proxy.aop.exception.AopRunException;
import com.sondertara.common.invocation.proxy.aop.exception.IllegalMethodSignatureException;
import com.sondertara.common.invocation.proxy.core.MethodInterceptor;
import com.sondertara.common.invocation.proxy.core.MethodMatcher;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;


/**
 * AOP工具类
 *
 * @author byx
 */
public class AopProxyUtils {
    /**
     * 存放一个方法拦截器的定义
     */
    private static class MethodInterceptorDefinition {
        private final Object advice;
        private final Method method;
        private final int order;

        private MethodInterceptorDefinition(Object advice, Method method) {
            this.advice = advice;
            this.method = method;
            if (method.isAnnotationPresent(Order.class)) {
                this.order = method.getAnnotation(Order.class).value();
            } else {
                this.order = 1;
            }
        }

        public int getOrder() {
            return order;
        }

        public MethodInterceptor build() {
            return getMethodInterceptor().when(getMethodMatcher());
        }

        /**
         * 解析拦截类型注解，生成方法拦截器
         */
        private MethodInterceptor getMethodInterceptor() {
            if (method.isAnnotationPresent(Before.class)) {
                return processBefore();
            } else if (method.isAnnotationPresent(After.class)) {
                return processAfter();
            } else if (method.isAnnotationPresent(Around.class)) {
                return processAround();
            } else if (method.isAnnotationPresent(Replace.class)) {
                return processReplace();
            } else if (method.isAnnotationPresent(AfterThrowing.class)) {
                return processAfterThrowing();
            } else {
                return MethodInterceptor.invokeTargetMethod();
            }
        }

        /**
         * 解析Filter注解，生成方法匹配器
         */
        private MethodMatcher getMethodMatcher() {
            MethodMatcher matcher = MethodMatcher.all();

            if (method.isAnnotationPresent(Filter.class)) {
                Filter filter = method.getAnnotation(Filter.class);

                if (!"".equals(filter.name())) {
                    matcher = matcher.andName(filter.name());
                } else if (!"".equals(filter.pattern())) {
                    matcher = matcher.andPattern(filter.pattern());
                }

                if (filter.returnType() != Filter.Dummy.class) {
                    matcher = matcher.andReturnType(filter.returnType());
                }
                if (filter.parameterTypes().length > 0) {
                    matcher = matcher.andParameterTypes(filter.parameterTypes());
                }
            }

            return matcher;
        }

        /**
         * 调用增强对象的方法
         * 增强对象的方法不允许抛出受检异常
         * 如果增强对象的方法抛出RuntimeException，则直接向外抛出
         */
        private Object callAdviceMethod(Object[] params) {
            try {
                method.setAccessible(true);
                return method.invoke(advice, params);
            } catch (IllegalAccessException e) {
                throw new AopRunException("Cannot invoke method: " + method, e);
            } catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                if (targetException instanceof RuntimeException) {
                    throw (RuntimeException) targetException;
                } else {
                    throw new AopRunException("Enhanced methods cannot throw checked exceptions: " + method);
                }
            }
        }

        /**
         * 解析Before注解
         */
        private MethodInterceptor processBefore() {
            if (method.getParameterCount() > 0) {
                if (method.getReturnType().isArray()) {
                    return MethodInterceptor.interceptParameters(params -> {
                        // 避免基本类型数组转换时的坑
                        // 一个基本类型的数组无法强制转换成Object[]
                        // 所以只能把返回的数组中每个元素单独拿出来
                        // 然后依次放入一个新的Object[]中
                        Object ret = callAdviceMethod(params);
                        int len = Array.getLength(ret);
                        Object[] arr = new Object[len];
                        for (int i = 0; i < len; ++i) {
                            arr[i] = Array.get(ret, i);
                        }
                        return arr;
                    });
                } else if (method.getReturnType() == void.class) {
                    return MethodInterceptor.interceptParameters(params -> {
                        callAdviceMethod(params);
                        return params;
                    });
                } else {
                    throw new IllegalMethodSignatureException(method, Before.class);
                }
            } else {
                return MethodInterceptor.interceptParameters(params -> {
                    callAdviceMethod(new Object[]{});
                    return params;
                });
            }
        }

        /**
         * 解析After注解
         */
        private MethodInterceptor processAfter() {
            if (method.getParameterCount() == 0) {
                return MethodInterceptor.interceptReturnValue(returnValue -> {
                    callAdviceMethod(new Object[]{});
                    return returnValue;
                });
            } else if (method.getParameterCount() == 1) {
                if (method.getReturnType() == void.class) {
                    return MethodInterceptor.interceptReturnValue(returnValue -> {
                        callAdviceMethod(new Object[]{returnValue});
                        return returnValue;
                    });
                } else {
                    return MethodInterceptor.interceptReturnValue(returnValue -> callAdviceMethod(new Object[]{returnValue}));
                }
            } else {
                throw new IllegalMethodSignatureException(method, After.class);
            }
        }

        /**
         * 解析Around注解
         */
        private MethodInterceptor processAround() {
            return targetMethod -> callAdviceMethod(new Object[]{targetMethod});
        }

        /**
         * 解析Replace注解
         */
        private MethodInterceptor processReplace() {
            return targetMethod -> callAdviceMethod(targetMethod.getParams());
        }

        private MethodInterceptor processAfterThrowing() {
            return MethodInterceptor.interceptException(t -> callAdviceMethod(new Object[]{t}));
        }
    }

    private static boolean isInterceptMethod(Method m) {
        return m.isAnnotationPresent(Before.class)
                || m.isAnnotationPresent(After.class)
                || m.isAnnotationPresent(Around.class)
                || m.isAnnotationPresent(Replace.class)
                || m.isAnnotationPresent(AfterThrowing.class);
    }

    /**
     * 获取AOP代理对象
     *
     * @param target  目标对象
     * @param advices 拦截器对象
     * @param <T>     返回类型
     * @return 已增强的对象
     */
    public static <T> T getAopProxy(T target, Object... advices) {
        return getAopProxy(target, ProxyType.AUTO, advices);
    }

    public static <T> T getAopProxy(T target, ProxyType type, Object... advices) {
        // 1. 获取增强类（advice）中的所有方法
        // 2. 解析方法上的注解，并封装成MethodInterceptorDefinition
        // 3. 根据order数值排序
        // 4. 把MethodInterceptorDefinition转换成MethodInterceptor
        // 5. 用then把所有方法拦截器连接起来，形成拦截器链
        MethodInterceptor interceptor = Arrays.stream(advices)
                .flatMap(advice -> Arrays
                        .stream(advice.getClass().getMethods())
                        .filter(AopProxyUtils::isInterceptMethod)
                        .map(m -> new MethodInterceptorDefinition(advice, m))
                        .sorted(Comparator.comparingInt(MethodInterceptorDefinition::getOrder))
                        .map(MethodInterceptorDefinition::build))
                .reduce(MethodInterceptor.invokeTargetMethod(), MethodInterceptor::then);

        switch (type) {
            case JDK:
                return ProxyUtils.proxy(target, interceptor, ProxyType.JDK);
            case BYTE_BUDDY:
                return ProxyUtils.proxy(target, interceptor, ProxyType.BYTE_BUDDY);
            default:
                return ProxyUtils.proxy(target, interceptor);
        }
    }
}
