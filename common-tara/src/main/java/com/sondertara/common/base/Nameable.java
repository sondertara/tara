package com.sondertara.common.base;

public interface Nameable extends Named, NameAware {
    @Override
    void setName(String name);

    @Override
    String getName();
}
