package com.sondertara.common.convert;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author jinuo.fang
 */
public class BooleanEvaluator {
    private final Set<String> trueFactors = new HashSet<>();
    private final Set<String> falseFactors = new HashSet<>();
    private boolean nullValue = false;
    private boolean stringIgnoreCase = true;

    public BooleanEvaluator(boolean nullValue, boolean stringIgnoreCase, Object[] trueFactors, Object[] falseFactors) {
        this(nullValue, stringIgnoreCase, Lists.asList(trueFactors), Lists.asList(falseFactors));
    }

    public BooleanEvaluator(boolean nullValue, boolean stringIgnoreCase, List<Object> trueFactors, List<Object> falseFactors) {
        setNullValue(nullValue);
        setStringIgnoreCase(stringIgnoreCase);
        addTrueFactors(trueFactors);
        addFalseFactors(falseFactors);
    }

    public void setStringIgnoreCase(boolean ignoreCase) {
        this.stringIgnoreCase = ignoreCase;
    }

    public void setNullValue(boolean nullValue) {
        this.nullValue = nullValue;
    }

    public void addFactor(Object trueFactor, Object falseFactor) {
        addTrueFactor(trueFactor);
        addFalseFactor(falseFactor);
    }

    public void addTrueFactors(List<Object> trueFactors) {
        if (Emptys.isNotEmpty(trueFactors)) {
            CollectionUtils.forEach(trueFactors, new Consumer<Object>() {
                @Override
                public void accept(Object object) {
                    addTrueFactor(object);
                }
            });
        }
    }

    public void addFalseFactors(List<Object> falseFactors) {
        if (Emptys.isNotEmpty(falseFactors)) {
            CollectionUtils.forEach(falseFactors, new Consumer<Object>() {
                @Override
                public void accept(Object object) {
                    addFalseFactor(object);
                }
            });
        }
    }

    public void addTrueFactor(Object trueFactor) {
        if (trueFactor != null) {
            String value = ConvertUtils.convert(String.class, trueFactor);
            trueFactors.add(stringIgnoreCase ? value.toLowerCase() : value);
        }
    }

    public void addFalseFactor(Object falseFactor) {
        if (falseFactor != null) {
            String value = ConvertUtils.convert(String.class, falseFactor);
            falseFactors.add(stringIgnoreCase ? value.toLowerCase() : value);
        }
    }

    public boolean evalTrue(Object object) {
        if (object == null) {
            return nullValue;
        }
        if (ObjectUtils.isEmpty(object)) {
            return false;
        }
        String value = ConvertUtils.convert(String.class, object);
        if (stringIgnoreCase) {
            value = value.toLowerCase();
        }
        if (trueFactors.contains(value)) {
            return true;
        }
        if (falseFactors.contains(value)) {
            return false;
        }
        return !nullValue;
    }

    public boolean evalFalse(Object object) {
        if (object == null) {
            return nullValue;
        }
        if (ObjectUtils.isEmpty(object)) {
            return nullValue;
        }
        String value = ConvertUtils.convert(String.class, object);
        if (stringIgnoreCase) {
            value = value.toLowerCase();
        }
        if (falseFactors.contains(value)) {
            return true;
        }
        if (trueFactors.contains(value)) {
            return false;
        }
        return nullValue;
    }

    public static BooleanEvaluator createTrueEvaluator(Object... truthArray) {
        return new BooleanEvaluator(false, true, truthArray, null);
    }

    public static BooleanEvaluator createTrueEvaluator(boolean nullValue, boolean stringIgnoreCase, Object[] truthArray) {
        return new BooleanEvaluator(nullValue, stringIgnoreCase, truthArray, null);
    }

    public static BooleanEvaluator createFalseEvaluator(Object... falseArray) {
        return new BooleanEvaluator(false, true, null, falseArray);
    }

    public static BooleanEvaluator createFalseEvaluator(boolean nullValue, boolean stringIgnoreCase, Object[] falseArray) {
        return new BooleanEvaluator(nullValue, stringIgnoreCase, null, falseArray);
    }

    public static final BooleanEvaluator SIMPLE_STRING_EVALUATOR = createTrueEvaluator("true");
}
