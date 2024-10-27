package com.sondertara.common.security.privileged;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.function.Supplier;

public class CommonPrivilegedAction<R> implements PrivilegedAction<R> {
    private Supplier<R> delegateAction;

    public CommonPrivilegedAction(Supplier<R> delegateAction) {
        this.delegateAction = delegateAction;
    }

    @Override
    public R run() {
        return delegateAction.get();
    }

    public static <R> CommonPrivilegedAction<R> of(Supplier<R> delegateAction) {
        return new CommonPrivilegedAction<R>(delegateAction);
    }

    public static <R> R doPrivileged(Supplier<R> supplier) {
        if (System.getSecurityManager() != null) {
            return AccessController.<R>doPrivileged(CommonPrivilegedAction.<R>of(supplier));
        }
        return supplier.get();
    }
}
