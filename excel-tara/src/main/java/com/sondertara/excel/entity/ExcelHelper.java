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

    /**
     * 文件名称,不带后缀，必填
     */
    private String fileName;
    /**
     * 操作空间,防止多个操作人文件混乱
     */
    private String user;
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

    private ExcelHelper(String fileName, String user, Integer pageStart, Integer pageEnd, Integer pageSize) {
        this.fileName = fileName;
        this.user = user;
        this.pageStart = pageStart;
        this.pageEnd = pageEnd;
        this.pageSize = pageSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String fileName;

        private String user;

        private Integer pageStart;

        private Integer pageEnd;

        private Integer pageSize;


        public Builder fileName(final String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder user(final String user) {
            this.user = user;
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
            return new ExcelHelper(this.fileName, this.user, this.pageStart, this.pageEnd, this.pageSize);
        }
    }
}
