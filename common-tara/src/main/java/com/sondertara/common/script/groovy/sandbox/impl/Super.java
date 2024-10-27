package com.sondertara.common.script.groovy.sandbox.impl;


import com.sondertara.common.script.groovy.sandbox.GroovyInterceptor;

/**
 * Packs argument of the super method call for {@link GroovyInterceptor.Invoker}
 * @author Kohsuke Kawaguchi
 */
public final class Super {
    final Class senderType;
    final Object receiver;

    public Super(Class senderType, Object receiver) {
        this.senderType = senderType;
        this.receiver = receiver;
    }
}
