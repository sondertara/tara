package com.sondertara.common.bit;

import com.sondertara.common.base.Assert;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
public final class Masks {
    private Masks() {
    }

    public static <T> int createMask(Collection<T> operandHolders, Function<T, Integer> mapper) {

        Collection<Integer> operands = operandHolders.stream().map(mapper).filter(Objects::nonNull).collect(Collectors.toList());
        return createMask(operands);
    }

    public static int createMask(int... operands) {
        return createMask(Lists.asList(CollectionUtils.<Integer>asIterable(operands)));
    }

    /**
     * @param operands 操作数集合
     * @return 掩码
     */
    public static int createMask(Collection<Integer> operands) {
        Objects.requireNonNull(operands, "operand is required");
        Assert.isFalse(ObjectUtils.isNotEmpty(operands), "operands is required");
        int mask = 0;
        for (int operand : operands) {
            mask = addOperand(mask, operand);
        }
        return mask;
    }

    public static int addOperand(int mask, int operand) {
        return mask | operand;
    }

    public static int removeOperand(int mask, int operand) {
        return mask & ~operand;
    }


    /**
     * 判断 mask 是否包含 指定的操作数 operand
     *
     * @param mask    掩码
     * @param operand 操作数
     * @return 包含与否
     */
    public static boolean containsOperand(int mask, int operand) {
        return (mask & operand) == operand;
    }


}