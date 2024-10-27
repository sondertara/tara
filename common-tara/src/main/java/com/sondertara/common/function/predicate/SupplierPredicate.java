package com.sondertara.common.function.predicate;


import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class SupplierPredicate<V> implements Predicate<Supplier<V>> {
    @Override
    public boolean test(Supplier<V> valueSupplier){
        return doTest(valueSupplier.get());
    }

    protected abstract boolean doTest(V value);
}
