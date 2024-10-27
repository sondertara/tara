package com.sondertara.common.invocation.proxy.sparse;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.function.Factory;
import com.sondertara.common.invocation.proxy.core.MethodInterceptor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Proxy;
import java.util.Objects;

public class SparseProxyFactory implements Factory<Object, Object> {
    @Override
    public Object get(Object target) {
        return newProxy(target, null, null);
    }

    public static Object newProxy(@NonNull Object target, @Nullable MethodInterceptor interceptor, @Nullable Class[] interfaces) {
        Objects.requireNonNull(target);

        if (Emptys.isEmpty(interfaces)) {
            interfaces = target.getClass().getInterfaces();
        }


        SparseMethodInvocationHandler handler = new SparseMethodInvocationHandler(target);
        handler.setInterceptor(interceptor);
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), interfaces, handler);
    }
}
