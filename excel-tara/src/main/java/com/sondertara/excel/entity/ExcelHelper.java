package com.sondertara.excel.entity;

import com.sondertara.excel.constants.Constants;

import java.io.Serializable;

/**
 * @author SonderTara
 */
public class ExcelHelper implements Serializable {
    private final Integer recordCountPerSheet;
    private final Boolean openAutoColumWidth;
    private final Integer rowAccessWindowSize;

    public static ExcelHelperBuilder builder() {
        return new ExcelHelperBuilder();
    }

    public Integer getRecordCountPerSheet() {
        return this.recordCountPerSheet;
    }

    public Boolean getOpenAutoColumWidth() {
        return this.openAutoColumWidth;
    }

    public Integer getRowAccessWindowSize() {
        return this.rowAccessWindowSize;
    }

    public ExcelHelper(Integer recordCountPerSheet, Boolean openAutoColumWidth, Integer rowAccessWindowSize) {
        this.recordCountPerSheet = recordCountPerSheet;
        this.openAutoColumWidth = openAutoColumWidth;
        this.rowAccessWindowSize = rowAccessWindowSize;
    }

    public static class ExcelHelperBuilder {
        private Integer recordCountPerSheet;
        private Boolean openAutoColumWidth;
        private Integer rowAccessWindowSize;

        ExcelHelperBuilder() {
        }

        public ExcelHelperBuilder recordCountPerSheet(Integer recordCountPerSheet) {
            this.recordCountPerSheet = recordCountPerSheet;
            return this;
        }

        public ExcelHelperBuilder openAutoColumWidth(Boolean openAutoColumWidth) {
            this.openAutoColumWidth = openAutoColumWidth;
            return this;
        }

        public ExcelHelperBuilder rowAccessWindowSize(Integer rowAccessWindowSize) {
            this.rowAccessWindowSize = rowAccessWindowSize;
            return this;
        }

        public ExcelHelper build() {

            if (this.openAutoColumWidth == null) {
                this.openAutoColumWidth = Constants.OPEN_AUTO_COLUMN_WIDTH;
            }
            if (this.rowAccessWindowSize == null) {
                this.rowAccessWindowSize = Constants.DEFAULT_ROW_ACCESS_WINDOW_SIZE;
            }
            if (this.recordCountPerSheet == null) {
                this.recordCountPerSheet = Constants.DEFAULT_RECORD_COUNT_PEER_SHEET;
            }
            return new ExcelHelper(this.recordCountPerSheet, this.openAutoColumWidth, this.rowAccessWindowSize);
        }

        @Override
        public String toString() {
            return "ExcelHelper.ExcelHelperBuilder(recordCountPerSheet=" + this.recordCountPerSheet
                    + ", openAutoColumWidth=" + this.openAutoColumWidth + ", rowAccessWindowSize="
                    + this.rowAccessWindowSize + ")";
        }
    }
}
