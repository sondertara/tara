package com.sondertara.common.base;

/**
 * 克隆
 *
 * @author Jiahang Li
 * @version 1.0.0
 *  */
public abstract class CloneSupport<T> implements Cloneable {

    @SuppressWarnings("unchecked")
    @Override
    public T clone() {
        try {
            return (T) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

}
