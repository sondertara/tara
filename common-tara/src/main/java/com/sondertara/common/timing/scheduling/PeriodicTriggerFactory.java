package com.sondertara.common.timing.scheduling;

import com.sondertara.common.convert.BooleanEvaluator;
import com.sondertara.common.math.Numbers;
import com.sondertara.common.regex.RegexUtils;
import com.sondertara.common.regex.Regexp;
import com.sondertara.common.regex.RegexpMatcher;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.text.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * {initialDelay} {fixedRate} {period} {timeunit}
 *
 *  */
public class PeriodicTriggerFactory implements TriggerFactory {
    private static final String NAME = "periodic";
    private static final Regexp REGEXP = RegexUtils.createRegexp("(?:(?:(?<initialDelay>\\d+)?\\s)?(?:(?<fixedRate>true|false)?\\s))?(?:(?<period>\\d+)\\s)(?:(?<timeunit>nanoseconds|microseconds|milliseconds|seconds|minutes|hours|days))");

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Trigger get(String expression) {
        RegexpMatcher matcher = REGEXP.matcher(expression);
        if (matcher.matches()) {
            String initialDelayString = matcher.group("initialDelay");
            String fixedRateString = matcher.group("fixedRate");
            String periodString = matcher.group("period");
            String timeunitString = matcher.group("timeunit");

            long initialDelay = StringUtils.isBlank(initialDelayString) ? 0 : Numbers.createLong(StringUtils.trim(initialDelayString));
            boolean fixedRate = BooleanEvaluator.SIMPLE_STRING_EVALUATOR.evalTrue(fixedRateString);
            long period = StringUtils.isBlank(periodString) ? 0 : Numbers.createLong(StringUtils.trim(periodString));
            TimeUnit timeUnit = TimeUnit.valueOf(StringUtils.upperCase(timeunitString));

            PeriodicTrigger periodicTrigger = new PeriodicTrigger(period, timeUnit);
            periodicTrigger.setInitialDelay(initialDelay);
            periodicTrigger.setFixedRate(fixedRate);
            return periodicTrigger;
        }
        throw new IllegalArgumentException(StringUtils.format("illegal periodic trigger expression: {}", expression));
    }
}
