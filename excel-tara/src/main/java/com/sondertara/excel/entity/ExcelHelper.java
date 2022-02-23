package com.sondertara.excel.entity;


import lombok.Data;

import java.io.Serializable;

/**
 * @author huangxiaohu
 */
@Data
public class ExcelHelper implements Serializable {

    private ExcelHelper() {
    }

    /***
     * 开始页码，不填默认是1
     */
    private Integer pageStart;
    /**
     * 截止页码，为空会导出所有查询的数据
     */
    private Integer pageEnd;
    /**
     * 分页大小默认2000
     */
    private Integer pageSize;
    /**
     * 工作空间，excel生成的目录空间
     */
    private String workspace;

    /**
     * enable open cell auto column width ,to keep high performance has removed.
     */
    private Boolean openAutoColumWidth;

    public ExcelHelper(String workspace, Integer pageStart, Integer pageEnd, Integer pageSize, Boolean openAutoColumWidth) {
        this.pageStart = pageStart;
        this.pageEnd = pageEnd;
        this.pageSize = pageSize;
        this.workspace = workspace;
        this.openAutoColumWidth = openAutoColumWidth;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {


        private String workspace;

        private Integer pageStart;

        private Integer pageEnd;

        private Integer pageSize;

        private Boolean openAutoColumWidth = false;


        public Builder openAutoColumWidth(final Boolean openAutoColumWidth) {
            this.openAutoColumWidth = openAutoColumWidth;
            return this;
        }


        public Builder workspace(final String workspace) {
            this.workspace = workspace;
            return this;
        }

        public Builder pageStart(final Integer pageStart) {
            this.pageStart = pageStart;
            return this;
        }

        public Builder pageEnd(final Integer pageEnd) {
            this.pageEnd = pageEnd;
            return this;
        }

        public Builder pageSize(final Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public ExcelHelper build() {
            return new ExcelHelper(this.workspace, this.pageStart, this.pageEnd, this.pageSize, this.openAutoColumWidth);
        }
    }
}
