package com.sondertara.common.timing.scheduling;

import com.sondertara.common.function.Builder;
import com.sondertara.common.regex.RegexUtils;
import com.sondertara.common.regex.Regexp;
import com.sondertara.common.regex.RegexpMatcher;
import com.sondertara.common.text.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;


/**
 *  */
public class TriggerBuilder implements Builder<Trigger> {
    private static Regexp EXPRESSION_REGEXP = RegexUtils.createRegexp("(?<type>\\w+)(?::(?<exp>.*))?");

    /**
     * 带有 trigger 类型的 expression
     */
    @NonNull
    private String expression;

    @Nullable
    private TriggerFactoryRegistry registry;

    public TriggerBuilder expression(String expression) {
        if (expression != null) {
            this.expression = StringUtils.trimToNull(expression);
        }
        return this;
    }

    public TriggerBuilder registry(TriggerFactoryRegistry registry) {
        this.registry = registry;
        return this;
    }


    @Override
    public Trigger build() {
        if (StringUtils.isBlank(expression)) {
            throw new IllegalArgumentException(StringUtils.format("illegal trigger expression: {}", expression));
        }
        RegexpMatcher matcher = EXPRESSION_REGEXP.matcher(expression);
        String triggerType;
        String exp;
        if (matcher.matches()) {
            triggerType = matcher.group("type");
            exp = matcher.group("exp");
        } else {
            throw new IllegalArgumentException(StringUtils.format("illegal trigger expression: {}", expression));
        }
        if (StringUtils.isBlank(triggerType)) {
            throw new IllegalArgumentException(StringUtils.format("illegal trigger expression: {}, unrecognized trigger type: {}", expression, triggerType));
        }
        if (registry == null) {
            registry = TriggerFactoryRegistry.GLOBAL_TRIGGER_REGISTRY;
        }

        TriggerFactory factory = registry.get(triggerType);
        if (factory == null) {
            throw new IllegalArgumentException(StringUtils.format("illegal trigger expression: {}, unrecognized trigger type: {}", expression, triggerType));
        }
        return factory.get(exp);
    }
}
