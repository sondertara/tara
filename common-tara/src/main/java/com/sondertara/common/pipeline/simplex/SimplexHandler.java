package com.sondertara.common.pipeline.simplex;

import java.util.function.Function;

public interface SimplexHandler<IN, OUT> extends Function<IN, OUT> {
    OUT apply(IN in);
}
