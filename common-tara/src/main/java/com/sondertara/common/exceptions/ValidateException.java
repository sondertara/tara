package com.sondertara.common.exceptions;

import lombok.Data;

/**
 * 校验异常
 *
 * @author chenxinshi
 * @date 2019/9/5 上午11:17
 */
@Data
public class ValidateException extends RuntimeException {

    private String code;

    public ValidateException(String msg) {
        super(msg);
    }

    public ValidateException(String code, String msg) {
        super(msg);
        this.code = code;
    }
}
