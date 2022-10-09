package com.sondertara.excel.entity;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author huangxiaohu
 */
@Getter
public class PageQueryParam implements Serializable {

    private final Integer pageSize;

    private final Integer pageStart;
    private Integer pageEnd = -1;

    public static Builder builder() {
        return new Builder();
    }

    private PageQueryParam(Integer pageSize, Integer pageStart, Integer pageEnd) {
        this.pageSize = pageSize;
        this.pageStart = pageStart;
        this.pageEnd = pageEnd;
    }

    @Override
    public String toString() {
        return "PageQueryDTO{" + "pageSize=" + pageSize + ", pageStart=" + pageStart + ", pageEnd=" + pageEnd + '}';
    }

    public static class Builder {

        private Integer pageSize;

        private Integer pageStart;
        private Integer pageEnd;

        public Builder pageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder pageStart(Integer pageStart) {
            this.pageStart = pageStart;
            return this;
        }

        public Builder pageEnd(Integer pageEnd) {
            this.pageEnd = pageEnd;
            return this;
        }

        public PageQueryParam build() {

            if (this.pageStart == null) {
                this.pageStart = 1;
            }
            if (this.pageEnd == null) {
                this.pageEnd = Integer.MAX_VALUE;
            }
            if (this.pageSize == null) {
                this.pageSize = 2000;

            }
            return new PageQueryParam(this.pageSize, this.pageStart, this.pageEnd);
        }
    }

}
