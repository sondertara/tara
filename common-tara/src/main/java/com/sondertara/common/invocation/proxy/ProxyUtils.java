package com.sondertara.common.invocation.proxy;


import com.sondertara.common.invocation.proxy.core.Invokable;
import com.sondertara.common.invocation.proxy.core.MethodInterceptor;
import com.sondertara.common.invocation.proxy.core.MethodSignature;
import com.sondertara.common.invocation.proxy.core.TargetMethod;
import com.sondertara.common.invocation.proxy.exception.ProxyRunException;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

/**
 * 动态代理工具类
 *
 * @author byx
 */
public class ProxyUtils {
    /**
     * 创建代理对象
     * 自动选择代理方式
     *
     * @param target      目标对象
     * @param interceptor 方法拦截器
     * @param <T>         返回类型
     * @return 被增强的代理对象
     */
    public static <T> T proxy(Object target, MethodInterceptor interceptor) {
        // final类使用jdk代理
        if (Modifier.isFinal(target.getClass().getModifiers())) {
            return proxyByJdk(target.getClass().getInterfaces(), target, interceptor);
        }
        // 没有接口的类使用ByteBuddy代理
        else if (target.getClass().getInterfaces().length == 0) {
            return proxyByByteBuddy(target.getClass(), target, interceptor);
        }
        // 其他情况使用jdk代理
        else {
            return proxyByJdk(target.getClass().getInterfaces(), target, interceptor);
        }
    }

    public static Class<?> getProxyClass(Class<?>... interfaces) {
        return Proxy.getProxyClass(Thread.currentThread().getContextClassLoader(), interfaces);
    }

    public static Class<?> getProxyClass(ClassLoader loader, Class<?>... interfaces) {
        return Proxy.getProxyClass(loader, interfaces);
    }

    public static boolean isProxyClass(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        return Proxy.isProxyClass(clazz);
    }

    public static Object newProxyInstance(InvocationHandler invocationHandler, Class<?>[] interfaces) {
        return newProxyInstance(null, invocationHandler, interfaces);
    }

    public static Object newProxyInstance(ClassLoader loader, InvocationHandler invocationHandler, Class<?>[] interfaces) {
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        return Proxy.newProxyInstance(loader, interfaces, invocationHandler);
    }

    public static InvocationHandler getInvocationHandler(Object proxyInstance) {
        return Proxy.getInvocationHandler(proxyInstance);
    }

    /**
     * 创建代理对象
     * 手动选择代理方式（JDK、ByteBuddy）
     *
     * @param target      目标对象
     * @param interceptor 方法拦截器
     * @param type        代理类型
     * @param <T>         返回类型
     * @return 被增强的代理对象
     */
    public static <T> T proxy(Object target, MethodInterceptor interceptor, ProxyType type) {
        switch (type) {
            case JDK:
                return proxyByJdk(target.getClass().getInterfaces(), target, interceptor);
            case BYTE_BUDDY:
                return proxyByByteBuddy(target.getClass(), target, interceptor);
            default:
                return proxy(target, interceptor);
        }
    }

    /**
     * 动态实现接口
     *
     * @param interfaceType 接口类型
     * @param interceptor   方法拦截器
     * @param <T>           返回类型
     * @return 动态生成的接口实现类
     */
    public static <T> T implement(Class<T> interfaceType, MethodInterceptor interceptor) {
        return proxyByJdk(new Class[]{interfaceType}, null, interceptor);
    }

    /**
     * 动态生成子类
     *
     * @param superClass  父类
     * @param interceptor 方法拦截器
     * @param <T>         返回类型
     * @return 动态生成的子类
     */
    public static <T> T extend(Class<T> superClass, MethodInterceptor interceptor) {
        return proxyByByteBuddy(superClass, null, interceptor);
    }

    /**
     * 使用jdk动态代理
     */
    @SuppressWarnings("unchecked")
    private static <T> T proxyByJdk(Class<?>[] interfaceTypes, Object target, MethodInterceptor interceptor) {
        return (T) Proxy.newProxyInstance(
                ProxyUtils.class.getClassLoader(),
                interfaceTypes,
                (proxy, method, args) -> {
                    Method targetMethod = method;
                    if (target != null) {
                        targetMethod = target.getClass()
                                .getMethod(method.getName(), method.getParameterTypes());
                    }
                    return interceptor.intercept(
                            new TargetMethod(
                                    MethodSignature.of(targetMethod),
                                    Invokable.of(targetMethod, target),
                                    args
                            )
                    );
                });
    }

    /**
     * 使用ByteBuddy动态代理
     */
    @SuppressWarnings({"unchecked"})
    private static <T> T proxyByByteBuddy(Class<?> superclass, Object target, MethodInterceptor interceptor) {
        try {
            return (T) new ByteBuddy()
                    .subclass(superclass)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(new ByteBuddyInterceptor(target, interceptor)))
                    .make()
                    .load(ProxyUtils.class.getClassLoader())
                    .getLoaded()
                    .getConstructor()
                    .newInstance();
        } catch (Exception e) {
            throw new ProxyRunException("Cannot proxy by ByteBuddy: " + e.getMessage(), e);
        }
    }

    /**
     * ByteBuddy方法拦截器
     */
    public static class ByteBuddyInterceptor {
        private final Object target;
        private final MethodInterceptor interceptor;

        private ByteBuddyInterceptor(Object target, MethodInterceptor interceptor) {
            this.target = target;
            this.interceptor = interceptor;
        }

        @RuntimeType
        public Object intercept(@Origin Method method, @AllArguments Object[] args) {
            return interceptor.intercept(
                    new TargetMethod(
                            MethodSignature.of(method),
                            Invokable.of(method, target),
                            args
                    )
            );
        }
    }
}
