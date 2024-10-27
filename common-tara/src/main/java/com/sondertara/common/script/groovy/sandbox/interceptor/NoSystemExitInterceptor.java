package com.sondertara.common.script.groovy.sandbox.interceptor;

import com.sondertara.common.script.groovy.sandbox.GroovyValueFilter;

/**
 * Reject any static calls to {@link System}.
 *
 * @author Kohsuke Kawaguchi
 **/
public class NoSystemExitInterceptor extends GroovyValueFilter {
    @Override
    public Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args)
        throws Throwable {
        if (receiver == System.class && method == "exit") {
            throw new SecurityException("No call on System.exit() please");
        }
        return super.onStaticCall(invoker, receiver, method, args);
    }
}
