package com.sondertara.common.io;

import java.util.List;

public interface Closer<I> {
    void close(I i);
    List<Class> applyTo();
}
