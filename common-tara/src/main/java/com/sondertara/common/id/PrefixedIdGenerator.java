package com.sondertara.common.id;

import com.sondertara.common.base.Delegatable;
import com.sondertara.common.function.supplier.PrefixSupplier;

/**
 *
 */
public class PrefixedIdGenerator<E> implements IdGenerator<String>, Delegatable<IdGenerator<E>> {
    private IdGenerator<E> delegate;

    private PrefixSupplier prefixSupplier;

    private String separator;

    public PrefixedIdGenerator(IdGenerator<E> delegate, PrefixSupplier supplier) {
        this(delegate, supplier, "_");
    }

    public PrefixedIdGenerator(IdGenerator<E> delegate, PrefixSupplier supplier, String separator) {
        this.prefixSupplier = supplier;
        this.separator = separator;
        setDelegate(delegate);
    }


    @Override
    public String get() {
        E id = delegate.get();
        String prefix = prefixSupplier.apply(id);
        return prefix + separator + id;
    }

    @Override
    public IdGenerator<E> getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(IdGenerator<E> delegate) {
        this.delegate = delegate;
    }
}
