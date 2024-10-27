package com.sondertara.common.equator;

import com.sondertara.common.enums.CommonEnum;
import com.sondertara.common.enums.EnumDelegate;

public enum DifferType implements CommonEnum<Integer> {
    /**
     *
     */
    ADDED(1, "added"), REMOVED(2, "removed"), UPDATED(4, "updated"), EQUALED(8, "equaled");

    private final EnumDelegate enumDelegate;

    DifferType(int code, String name) {
        this.enumDelegate = new EnumDelegate(code, name, name);
    }

    @Override
    public String getName() {
        return this.enumDelegate.getName();
    }

    @Override
    public Integer getCode() {
        return this.enumDelegate.getCode();
    }

    @Override
    public String getDisplayText() {
        return this.enumDelegate.getDisplayText();
    }
}
