package com.sondertara.excel.enums;

/**
 * @author huangxiaohu
 */

public enum FieldRangeType {
    /**
     * range[]
     */
    RANGE_CLOSE(1),
    /**
     * range()
     */
    RANGE_OPEN(2),
    /**
     * range(]
     */
    RANGE_LEFT_OPEN(3),
    /**
     * range[)
     */
    RANGE_RIGHT_OPEN(4),
    ;

    private Integer type;

    FieldRangeType(Integer type) {
        this.type = type;
    }
}
