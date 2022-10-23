package com.sondertara.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页结果
 *
 * @author huangxiaohu
 * @date 2021/7/21 12:43
 */
@Data
public class PageResult<T> implements Serializable {

    private Integer page;
    private Integer pageSize;
    private Long total;
    private List<T> data;

    private PageResult(List<T> data) {
        if (null == data) {
            this.data = new ArrayList<>();
        } else {
            this.data = data;
        }
    }

    public PageResult(List<T> data, Long total, Integer page, Integer pageSize) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.data = data;
    }

    public static <T> PageResult<T> of(List<T> data) {
        return new PageResult<>(data);
    }

    public PageResult<T> pagination(Integer page, Integer pageSize) {
        this.page = page;
        this.pageSize = pageSize;
        return this;
    }

    public PageResult<T> total(Long total) {
        this.total = total;
        return this;
    }

    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    public int endIndex() {
        return (int) Math.ceil(this.total * 1.0f / pageSize) - 1;
    }

}
