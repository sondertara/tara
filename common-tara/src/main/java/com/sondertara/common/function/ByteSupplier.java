package com.sondertara.common.function;

/**
 * byte supplier
 *
 * @author Jiahang Li
 * @version 1.0.0
 *  */
@FunctionalInterface
public interface ByteSupplier {

    /**
     * 获取 byte 值
     *
     * @return byte
     */
    byte getAsByte();

}
