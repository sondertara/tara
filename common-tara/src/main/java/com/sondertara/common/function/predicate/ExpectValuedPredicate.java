package com.sondertara.common.function.predicate;

public abstract class ExpectValuedPredicate<V> extends SupplierPredicate<V> {
    private V expectedValue;

    @Override
    protected abstract boolean doTest(V actualValue);

    public V getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(V expectedValue) {
        this.expectedValue = expectedValue;
    }
}
