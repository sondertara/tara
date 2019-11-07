package org.cherubim.common.exceptions;

import lombok.Data;

/**
 * 业务异常
 *
 * @author chenxinshi
 * @date 2019/9/5 上午11:17
 */
@Data
public class BusinessException extends RuntimeException {

    private String code;

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(String code, String msg) {
        super(msg);
        this.code = code;
    }
}
