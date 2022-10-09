package com.sondertara.excel.entity;

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

    private final Integer page;
    private final Integer pageSize;
    private final Long total;
    private final List<T> data;

    public PageResult(Integer page, Integer pageSize, Long total, List<T> data) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        if (null == data) {
            this.data = new ArrayList<T>();
        } else {
            this.data = data;
        }
    }
}
