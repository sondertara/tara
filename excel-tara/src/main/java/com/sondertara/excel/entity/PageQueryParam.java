package com.sondertara.excel.entity;

import java.io.Serializable;

/**
 * @author huangxiaohu
 */

public class PageQueryParam implements Serializable {

    private Integer pageSize;

    private Integer pageStart;

    @Override
    public String toString() {
        return "PageQueryDTO{" + "pageSize=" + pageSize + ", pageStart=" + pageStart + ", pageEnd=" + pageEnd + '}';
    }

    private Integer pageEnd;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageStart() {
        return pageStart;
    }

    public void setPageStart(Integer pageStart) {
        this.pageStart = pageStart;
    }

    public Integer getPageEnd() {
        return pageEnd;
    }

    public void setPageEnd(Integer pageEnd) {
        this.pageEnd = pageEnd;
    }

    public PageQueryParam() {
        this.pageStart = 1;
        this.pageSize = 2000;
        this.pageEnd = Integer.MAX_VALUE;
    }
}
