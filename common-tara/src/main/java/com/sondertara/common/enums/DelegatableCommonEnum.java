package com.sondertara.common.enums;

import com.sondertara.common.base.Delegatable;

/**
 * common enum template
 */
class DelegatableCommonEnum implements CommonEnum<Integer>, Delegatable<EnumDelegate> {
    private EnumDelegate delegate;

    @Override
    public Integer getCode() {
        return delegate.getCode();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getDisplayText() {
        return delegate.getDisplayText();
    }


    @Override
    public EnumDelegate getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(EnumDelegate delegate) {
        this.delegate = delegate;
    }
}
