package com.sondertara.common.collection.graph;

import com.sondertara.common.enums.CommonEnum;
import com.sondertara.common.enums.EnumDelegate;

public enum VisitStatus implements CommonEnum<Integer> {
    NOT_VISITED(0, "notVisited", "未访问"),
    VISITING(1, "visiting", "访问中"),
    VISITED(2, "visited", "已访问");

    private EnumDelegate delegate;

    VisitStatus(int code, String name, String displayText) {
        this.delegate = new EnumDelegate(code, name, displayText);
    }

    @Override
    public Integer getCode() {
        return this.delegate.getCode();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public String getDisplayText() {
        return this.delegate.getDisplayText();
    }
}
