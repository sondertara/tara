package com.sondertara.common.script.groovy.sandbox.interceptor;

import com.sondertara.common.script.groovy.sandbox.GroovyInterceptor;

/**
 * @author: kevin.luy@antfin.com
 * @create: 2020-02-17 16:08
 **/
public class NoReflectionAllowedInterceptor extends GroovyInterceptor {

    @Override
    public Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
        if (receiver == Class.class && method == "newInstance") {
            throw new SecurityException("No call on Class.newInstance() please");
        }
        if (receiver == Class.class && method == "forName") {
            throw new SecurityException("No call on Class.forName(..) please");
        }
        if(receiver.getName().startsWith("java.lang.reflect.")){
            throw new SecurityException("deny to use java.lang.reflect.* !");
        }
        return super.onStaticCall(invoker, receiver, method, args);
    }
}
