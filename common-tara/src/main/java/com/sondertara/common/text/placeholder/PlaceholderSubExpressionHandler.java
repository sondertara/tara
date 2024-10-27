package com.sondertara.common.text.placeholder;

import com.sondertara.common.function.Consumer3;
import com.sondertara.common.struct.Holder;

public interface PlaceholderSubExpressionHandler extends Consumer3<String, String, Holder<String>> {
    @Override
    void accept(String variable, String expression, Holder<String> variableValueHolder);
}
