package com.sondertara.common.bean;

public interface ModelMapper<Source, Target> {
    Target map(Source a);
}
