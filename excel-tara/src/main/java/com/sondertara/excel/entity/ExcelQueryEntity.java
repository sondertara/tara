package com.sondertara.excel.entity;

import lombok.Data;

import java.util.List;

/**
 * 查询的分页数据
 *
 * @param <T> pojo
 * @author huangxiaohu
 */
@Data
public class ExcelQueryEntity<T> {

    /**
     * 数据
     */
    private List<T> data;
    /**
     * 页码
     */
    private Integer page;
}
