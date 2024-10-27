package com.sondertara.common.struct;


import com.sondertara.common.hash.Hashed;

public interface Reference<T> extends Hashed {
    T get();

    boolean isNull();

}
