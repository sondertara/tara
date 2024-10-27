package com.sondertara.common.base;

public interface Ordered {
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    int getOrder();
}
