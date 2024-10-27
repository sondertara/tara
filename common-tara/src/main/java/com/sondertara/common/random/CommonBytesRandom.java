package com.sondertara.common.random;

import com.sondertara.common.base.Delegatable;
import com.sondertara.common.base.Valid;

/**
 *  */
public class CommonBytesRandom implements BytesRandom, Delegatable<IRandom> {
    private IRandom delegate;
    private int multiplier = 1;

    @Override
    public IRandom getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(IRandom delegate) {
        this.delegate = delegate;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        Valid.isTrue(multiplier >= 1, "multiplier >= 1, actual: {}", multiplier);
        this.multiplier = multiplier;
    }

    @Override
    public byte[] get(Integer size) {
        byte[] dest = new byte[size * multiplier];
        get(dest);
        return dest;
    }

    @Override
    public void get(byte[] dest) {
        Valid.notNull(dest, "dest");
        delegate.nextBytes(dest);
    }
}
