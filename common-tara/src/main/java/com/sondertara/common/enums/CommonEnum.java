package com.sondertara.common.enums;

import com.sondertara.common.base.Named;
import com.sondertara.common.text.StringUtils;

import java.util.EnumSet;

/**
 * @author huangxiaohu.1ih
 */
public interface CommonEnum<T> extends Named {
    T getCode();

    /**
     * 获取显示名称
     * @return 名称字符串
     */
    default String getDisplayText() {
        return getName();
    }

    /**
     * 通过code查询枚举
     *
     * @param tClass 枚举类
     * @param code   code
     * @param <T>    code类型
     * @param <E>    枚举项对应class
     * @return 枚举项
     */
    public static <T, E extends Enum<E> & CommonEnum<T>> E getByCode(Class<E> tClass, final T code) {
        return EnumSet.allOf(tClass).stream().filter(t -> t.getCode().equals(code)).findFirst().orElseThrow(() -> new IllegalArgumentException(StringUtils.format("Cannot find enum for class:{} by code:{}", tClass.getName(), code)));
    }

    /**
     * 通过name查询枚举
     *
     * @param tClass 枚举类
     * @param name   name
     * @param <T>    code类型
     * @param <E>    枚举项对应class
     * @return 枚举项
     */
    public static <T, E extends Enum<E> & CommonEnum<T>> E getByName(Class<E> tClass, final String name) {
        return EnumSet.allOf(tClass).stream().filter(t -> t.getName().equals(name)).findFirst().orElseThrow(() -> new IllegalArgumentException(StringUtils.format("Cannot find enum for class:{} by name:{}", tClass.getName(), name)));
    }

    /**
     * 通过name查询枚举
     *
     * @param tClass      枚举类
     * @param displayText name
     * @param <T>         code类型
     * @param <E>         枚举项对应class
     * @return 枚举项
     */
    public static <T, E extends Enum<E> & CommonEnum<T>> E getByDisplayText(Class<E> tClass, final String displayText) {
        return EnumSet.allOf(tClass).stream().filter(t -> t.getDisplayText().equals(displayText)).findFirst().orElseThrow(() -> new IllegalArgumentException(StringUtils.format("Cannot find enum for class:{} by name:{}", tClass.getName(), displayText)));
    }

}