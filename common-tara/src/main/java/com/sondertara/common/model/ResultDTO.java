package com.sondertara.common.model;


import lombok.Data;

import java.io.Serializable;

/**
 * @author huangxiaohu
 * @Date: 2018-12-29
 * @Time: 上午9:39
 */
@Data
public class ResultDTO<T> implements Serializable {

    /**
     * 结果
     */
    private Boolean success;
    /**
     * 错误信息
     */
    private String msg;
    /**
     * 状态码
     */
    private String code;
    /**
     * 返回信息
     */
    private T data;

    public static <T> ResultDTO<T> success(T data) {
        ResultDTO<T> r = new ResultDTO<T>();
        r.setData(data);
        r.setSuccess(true);
        r.setMsg("success");
        r.setCode("200");
        return r;
    }

    public static <T> ResultDTO<T> success(String code, String msg) {
        ResultDTO<T> r = new ResultDTO<T>();
        r.setSuccess(true);
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static <T> ResultDTO<T> success() {
        ResultDTO<T> r = new ResultDTO<T>();
        r.setSuccess(true);
        r.setCode("200");
        r.setMsg("success");
        return r;
    }

    public static <T> ResultDTO<T> fail(String code, String msg) {
        ResultDTO<T> r = new ResultDTO<T>();
        r.setSuccess(false);
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static <T> ResultDTO<T> fail(String msg) {
        ResultDTO<T> r = new ResultDTO<T>();
        r.setSuccess(false);
        r.setCode("500");
        r.setMsg(msg);
        return r;
    }
}
