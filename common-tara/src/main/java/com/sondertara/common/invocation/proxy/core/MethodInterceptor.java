package com.sondertara.common.invocation.proxy.core;


import com.sondertara.common.invocation.proxy.exception.TargetMethodException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 方法拦截器
 *
 * @author byx
 */
public interface MethodInterceptor {
    /**
     * 拦截
     *
     * @param targetMethod 目标方法
     * @return 返回值
     */
    Object intercept(TargetMethod targetMethod);

    /**
     * 执行目标方法
     *
     * @return 方法拦截器
     */
    static MethodInterceptor invokeTargetMethod() {
        return TargetMethod::invokeWithOriginalParams;
    }

    /**
     * 拦截参数
     *
     * @param interceptor 参数拦截器
     */
    static MethodInterceptor interceptParameters(ParametersInterceptor interceptor) {
        return targetMethod -> {
            Object[] params = targetMethod.getParams();
            return targetMethod.invoke(interceptor.intercept(params));
        };
    }

    /**
     * 拦截返回值
     *
     * @param interceptor 返回值拦截器
     */
    static MethodInterceptor interceptReturnValue(ReturnValueInterceptor interceptor) {
        return targetMethod -> interceptor.intercept(targetMethod.invokeWithOriginalParams());
    }

    /**
     * 拦截异常
     *
     * @param interceptor 异常拦截器
     */
    static MethodInterceptor interceptException(ExceptionInterceptor interceptor) {
        return targetMethod -> {
            try {
                return targetMethod.invokeWithOriginalParams();
            } catch (TargetMethodException e) {
                return interceptor.intercept(e.getTargetException());
            }
        };
    }

    /**
     * 将目标类的部分方法委托到代理类
     *
     * @param proxy 代理类
     */
    static MethodInterceptor delegateTo(Object proxy) {
        return targetMethod -> {
            Object[] params = targetMethod.getParams();
            MethodSignature signature = targetMethod.getSignature();
            Method m = null;
            try {
                m = proxy.getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes());
                m.setAccessible(true);
                return m.invoke(proxy, params);
            }
            // 代理对象中没有该方法，则调用目标对象方法
            catch (NoSuchMethodException | IllegalAccessException e) {
                return targetMethod.invokeWithOriginalParams();
            }
            // 代理对象的方法抛出异常
            catch (InvocationTargetException e) {
                throw new TargetMethodException(e.getCause(), m);
            }
        };
    }

    /**
     * 指定拦截条件
     *
     * @param matcher 方法匹配器
     */
    default MethodInterceptor when(MethodMatcher matcher) {
        return targetMethod -> {
            MethodSignature signature = targetMethod.getSignature();
            if (matcher.match(signature)) {
                return this.intercept(targetMethod);
            }
            return targetMethod.invokeWithOriginalParams();
        };
    }

    /**
     * 多重代理
     *
     * @param interceptor 方法拦截器
     */
    default MethodInterceptor then(MethodInterceptor interceptor) {
        return targetMethod -> interceptor.intercept(
                new TargetMethod(
                        targetMethod.getSignature(),
                        params -> this.intercept(
                                new TargetMethod(
                                        targetMethod.getSignature(),
                                        targetMethod.getInvokable(),
                                        params
                                )
                        ),
                        targetMethod.getParams()
                )
        );
    }
}
