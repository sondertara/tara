package com.sondertara.common.collection;


public interface Attributable {
    void setAttribute(String name, Object value);

    Object getAttribute(String name);

    boolean hasAttribute(String name);

    void removeAttribute(String name);

    Iterable<String> getAttributeNames();

}
