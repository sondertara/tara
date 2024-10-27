package com.sondertara.common.pattern.patternset;

import com.sondertara.common.base.Named;
import com.sondertara.common.function.Parser;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface PatternSetExpressionParser<PatternEntry extends Named> extends Parser<String, PatternSet<PatternEntry>> {
    PatternSet<PatternEntry> parse(@NonNull String expression);

    @NonNull
    String getSeparator();

    @Nullable
    String getExcludeFlag();
}
