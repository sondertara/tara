package com.sondertara.common.invocation.proxy.core;

import java.lang.reflect.Method;

/**
 * 目标方法
 *
 * @author huangxiaohu.1ih
 */
public class TargetMethod {
    private final MethodSignature signature;


    private final Invokable invokable;
    private final Object[] params;

    /**
     * 创建目标方法
     *
     * @param signature 方法签名
     * @param invokable 调用器
     * @param params    原始参数
     */
    public TargetMethod(MethodSignature signature, Invokable invokable, Object[] params) {
        this.signature = signature;
        this.invokable = invokable;
        this.params = params;
    }

    /**
     * 获取方法签名
     *
     * @return 方法签名
     */
    public MethodSignature getSignature() {
        return signature;
    }

    /**
     * 获取方法调用器
     *
     * @return 调用器
     */
    public Invokable getInvokable() {
        return invokable;
    }

    /**
     * 获取原始参数
     *
     * @return 原始参数数组
     */
    public Object[] getParams() {
        return params;
    }

    /**
     * 使用原始参数调用目标方法
     *
     * @return 返回值
     */
    public Object invokeWithOriginalParams() {
        return invokable.invoke(params);
    }

    /**
     * 使用特定参数调用原始方法
     *
     * @param params 参数
     * @return 返回值
     */
    public Object invoke(Object... params) {
        return invokable.invoke(params);
    }

    /**
     * 获取Method对象
     *
     * @return Method对象
     */
    public Method getMethod() {
        return signature.getMethod();
    }

}
