package com.sondertara.common.concurrent.threadlocal;

import java.util.Objects;

public class NamedInheritableThreadLocal<T> extends InheritableThreadLocal<T> {

    private final String name;


    /**
     * Create a new NamedInheritableThreadLocal with the given name.
     *
     * @param name a descriptive name for this ThreadLocal
     */
    public NamedInheritableThreadLocal(String name) {
        Objects.requireNonNull(name, "Name must not be empty");
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
