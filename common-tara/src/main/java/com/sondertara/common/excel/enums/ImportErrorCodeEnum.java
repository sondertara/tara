package com.sondertara.common.excel.enums;

import lombok.Getter;

/**
 * @Author: chenxinshi
 * @date: 2019/4/25
 * @desc: 导入错误编码枚举类
 */
public enum ImportErrorCodeEnum {

    /**
     * 必填项为空
     */
    NULL("NULL", "数据为空"),
    /**
     * 数值超出范围，如不满足>,>=,<,<=等条件
     */
    OUT_OF_RANGE("OUT_OF_RANGE", "数值超出范围"),
    /**
     * 数据类型不一致，如接收date类型而导入的数据类型无法转换成date类型
     */
    DATA_TYPE_ERROR("DATA_TYPE_ERROR", "数据类型错误"),
    /**
     * 针对字符串类型，超过最大长度限制
     */
    DATA_TOO_LONG("DATA_TOO_LONG", "数据长度超出限制"),
    /**
     * 无法识别的枚举值，如保险类型不为交强险、商业险、非车险其中之一
     * 不能根据名称映射成code，如无法根据保险公司名称查出对应code或无法根据城市名称查出code
     */
    UNKNOWN_DATA("UNKNOWN_DATA", "无法识别的枚举值或不能根据名称映射成code"),
    /**
     * 违反业务唯一约束，如保单号重复，业务id重复
     */
    DATA_DUPLICATED("DATA_DUPLICATED", "数据重复"),
    /**
     * 匹配不到对应数据或唯一数据，如匹配不到保单或对应的任务
     */
    MATCH_FAILED("MATCH_FAILED", "匹配失败"),
    /**
     * 正则表达式不匹配
     */
    REGEX_NOT_MATCH("REGEX_NOT_MATCH", "正则不匹配");

    @Getter
    private String code;

    @Getter
    private String name;

    ImportErrorCodeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
