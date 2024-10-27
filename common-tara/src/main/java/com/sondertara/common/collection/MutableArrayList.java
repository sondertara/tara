package com.sondertara.common.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 可转换的 ArrayList
 *
 * @author Jiahang Li
 * @version 1.0.0
 *  */
public class MutableArrayList<E> extends ArrayList<E> implements MutableList<E>, Serializable {

    private static final long serialVersionUID = 66823498912315567L;

    public MutableArrayList() {
        super();
    }

    public MutableArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public MutableArrayList(Collection<? extends E> c) {
        super(c);
    }

}
