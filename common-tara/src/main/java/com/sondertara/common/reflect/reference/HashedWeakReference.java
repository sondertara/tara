package com.sondertara.common.reflect.reference;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.struct.Reference;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class HashedWeakReference<V> extends WeakReference<V> implements Reference<V> {
    private int hash;

    public HashedWeakReference(V referent, Integer hash) {
        super(referent);
        setHash(hash == null ? (referent == null ? 0 : referent.hashCode()) : hash);
    }

    public HashedWeakReference(V referent, ReferenceQueue<? super V> q, Integer hash) {
        super(referent, q);
        setHash(hash == null ? (referent == null ? 0 : referent.hashCode()) : hash);
    }

    public int getHash() {
        return hash;
    }

    @Override
    public void setHash(int hash) {
        this.hash = hash;
    }

    @Override
    public boolean isNull() {
        return get() == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HashedWeakReference<?> that = (HashedWeakReference<?>) o;

        if( hash != that.hash){
            return false;
        }
        return ObjectUtils.equals(get(), that.get());
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
