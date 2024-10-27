package com.sondertara.html.components.table;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author huangxiaohu
 */
@Getter
@ToString
public class TableCell implements Serializable {

    private int rowIndex;
    private String columnName;
    private String value;

    private int rowSpan;

    private final int colspan;

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    TableCell(int rowIndex, String columnName, String value, int rowSpan, int colspan) {
        this.rowIndex = rowIndex;
        this.columnName = columnName;
        this.value = value;
        this.rowSpan = rowSpan;
        this.colspan = colspan;
    }

    public static TableCellBuilder builder() {
        return new TableCellBuilder();
    }

    public static class TableCellBuilder {
        private int rowIndex;
        private String columnName;
        private String value;
        private int rowSpan;

        private int colspan;

        TableCellBuilder() {
        }


        public TableCellBuilder rowIndex(int rowIndex) {
            this.rowIndex = rowIndex;
            return this;
        }

        public TableCellBuilder columnName(String columnName) {
            this.columnName = columnName;
            return this;
        }

        public TableCellBuilder value(String value) {
            this.value = value;
            return this;
        }

        public TableCellBuilder colspan(int colspan) {
            this.colspan = colspan;
            return this;
        }

        public TableCellBuilder rowSpan(int rowSpan) {
            this.rowSpan = rowSpan;
            return this;
        }


        public TableCell build() {
            return new TableCell(this.rowIndex, this.columnName, this.value, this.rowSpan, this.colspan);
        }

        @Override
        public String toString() {
            return "TableCell.TableCellBuilder(rowIndex=" + this.rowIndex + ", columnName=" + this.columnName + ", value=" + this.value + ", rowSpan=" + this.rowSpan + ", colspan=" + this.colspan + ")";
        }
    }

}
