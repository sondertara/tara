package com.sondertara.common.id;

import java.util.function.Supplier;

public interface IdGenerator<E> extends Supplier<E>{
    E get();
}
