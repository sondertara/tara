package com.sondertara.common.model;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 统一返回结果
 *
 * @author huangxiaohu
 * @date 2021-06-15 15:08
 */
@Data
@Slf4j
public class ResultDTO<T> implements Serializable {

    /**
     * 结果
     */
    private Boolean success;

    /**
     * 错误编码
     */
    private String code;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 返回信息
     */
    private T data;

    public static <T> ResultDTO<T> success(T data) {
        ResultDTO<T> r = new ResultDTO<T>();
        r.setData(data);
        r.setSuccess(true);
        r.setCode("200");
        r.setMsg("success");
        return r;
    }

    public static <T> ResultDTO<T> success() {
        ResultDTO<T> r = new ResultDTO<T>();
        r.setData(null);
        r.setSuccess(true);
        r.setCode("200");
        r.setMsg("success");
        return r;
    }

    public static <T> ResultDTO<T> success(String code, String msg) {
        ResultDTO<T> result = new ResultDTO<>();
        result.setSuccess(true);
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static <T> ResultDTO<T> fail(String code, String msg) {
        ResultDTO<T> result = new ResultDTO<>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static <T> ResultDTO<T> fail(String msg) {
        ResultDTO<T> result = new ResultDTO<>();
        result.setSuccess(false);
        result.setCode("400");
        result.setMsg(msg);
        return result;
    }

    /**
     * 添加成功操作
     *
     * @param consumer
     */
    public void ifSuccess(Consumer<T> consumer) {
        if (getSuccess()) {
            consumer.accept(data);
        }
    }

    /**
     * 添加失败操作
     *
     * @param consumer
     */
    public void ifFailure(Consumer<T> consumer) {
        if (!getSuccess()) {
            consumer.accept(data);
        }
    }

    public T orElse(T other) {
        return success ? data : other;
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (success) {
            return data;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * 添加成功处理
     *
     * @param mapper
     */
    public <R> R map(Function<T, R> mapper) {
        if (!getSuccess()) {
            log.error("Not Success:{}", msg);
            return null;
        }
        return mapper.apply(this.data);
    }

    /**
     * 添加成功处理
     *
     * @param consumer
     */
    public void handleSuccess(Consumer<T> consumer) {
        if (getSuccess()) {
            consumer.accept(data);
        }
    }

    public String toJsonString() {
        return JSON.toJSONString(this, JSONWriter.Feature.WriteMapNullValue);
    }
}
