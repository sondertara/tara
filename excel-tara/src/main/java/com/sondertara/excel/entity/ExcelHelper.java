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
     * 接收者
     */
    private String receiptUser;
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

    private ExcelHelper(String fileName, String receiptUser, Integer pageStart, Integer pageEnd, Integer pageSize) {
        this.fileName = fileName;
        this.receiptUser = receiptUser;
        this.pageStart = pageStart;
        this.pageEnd = pageEnd;
        this.pageSize = pageSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String fileName;

        private String receiptUser;

        private Integer pageStart;

        private Integer pageEnd;

        private Integer pageSize;


        public Builder fileName(final String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder receiptUser(final String receiptUser) {
            this.receiptUser = receiptUser;
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
            return new ExcelHelper(this.fileName, this.receiptUser, this.pageStart, this.pageEnd, this.pageSize);
        }
    }
}
