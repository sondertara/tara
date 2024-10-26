//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.sondertara.excel.entity;

import com.sondertara.excel.common.constants.ExcelExportConstants;
import com.sondertara.excel.enums.ExcelColBindType;
import com.sondertara.excel.utils.ColorUtils;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author huangxiaohu
 */
@Getter
@Setter
public class ExcelWriteSheetEntity implements Cloneable {
    private String sheetName;

    /**
     * 最后一列索引 zero-based
     */
    private int lastColIndex;
    protected int order;
    protected boolean autoColWidth;
    private boolean hasTitle;
    private int maxRowsPerSheet;
    private boolean rowStriped;
    private Color rowStripeColor;
    private int titleRowHeight;
    private int dataRowHeight;

    private int maxColWidth = ExcelExportConstants.MAX_COL_WIDTH;
    protected ExcelColBindType bindType;
    /**
     * excel属性
     */
    private List<ExcelCellEntity> propertyList;


    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException var2) {
            throw new IllegalStateException(var2);
        }
    }

    public ExcelWriteSheetEntity() {

    }

    ExcelWriteSheetEntity(String sheetName, int lastColIndex, int order, boolean autoColWidth, boolean hasTitle, int maxRowsPerSheet, boolean rowStriped, Color rowStripeColor, int titleRowHeight, int dataRowHeight, int maxColWidth, ExcelColBindType bindType, List<ExcelCellEntity> propertyList) {
        this.sheetName = sheetName;
        this.lastColIndex = lastColIndex;
        this.order = order;
        this.autoColWidth = autoColWidth;
        this.hasTitle = hasTitle;
        this.maxRowsPerSheet = maxRowsPerSheet;
        this.rowStriped = rowStriped;
        this.rowStripeColor = rowStripeColor;
        this.titleRowHeight = titleRowHeight;
        this.dataRowHeight = dataRowHeight;
        this.maxColWidth = maxColWidth;
        this.bindType = bindType;
        this.propertyList = propertyList;
    }

    public static ExcelWriteSheetEntityBuilder builder() {
        return new ExcelWriteSheetEntityBuilder();
    }

    public static class ExcelWriteSheetEntityBuilder {
        private String sheetName = "Sheet";
        /**
         * zero-based
         */
        private int lastColIndex;
        private int order;
        private boolean autoColWidth = false;
        private boolean hasTitle = ExcelExportConstants.HAS_TITLE;
        private int maxRowsPerSheet = ExcelExportConstants.MAX_PER_SHEET_COUNT;
        private boolean rowStriped = ExcelExportConstants.ROW_STRIPED;
        private Color rowStripeColor;
        private int titleRowHeight = ExcelExportConstants.TITLE_ROW_HEIGHT;
        private int dataRowHeight = ExcelExportConstants.DATA_ROW_HEIGHT;
        private ExcelColBindType bindType = ExcelColBindType.DEF_ORDER;
        private List<ExcelCellEntity> propertyList = new ArrayList<>();
        private int maxColWidth = ExcelExportConstants.MAX_COL_WIDTH;

        ExcelWriteSheetEntityBuilder() {
        }

        public ExcelWriteSheetEntityBuilder sheetName(String sheetName) {
            this.sheetName = sheetName;
            return this;
        }

        public ExcelWriteSheetEntityBuilder maxColWidth(int maxColWidth) {
            this.maxColWidth = maxColWidth;
            return this;
        }

        public ExcelWriteSheetEntityBuilder lastColIndex(int lastColIndex) {
            this.lastColIndex = lastColIndex;
            return this;
        }

        public ExcelWriteSheetEntityBuilder order(int order) {
            this.order = order;
            return this;
        }

        public ExcelWriteSheetEntityBuilder autoColWidth(boolean autoColWidth) {
            this.autoColWidth = autoColWidth;
            return this;
        }

        public ExcelWriteSheetEntityBuilder hasTitle(boolean hasTitle) {
            this.hasTitle = hasTitle;
            return this;
        }

        public ExcelWriteSheetEntityBuilder maxRowsPerSheet(int maxRowsPerSheet) {
            this.maxRowsPerSheet = maxRowsPerSheet;
            return this;
        }

        public ExcelWriteSheetEntityBuilder rowStriped(boolean rowStriped) {
            this.rowStriped = rowStriped;
            return this;
        }

        public ExcelWriteSheetEntityBuilder rowStripeColor(Color rowStripeColor) {
            this.rowStripeColor = rowStripeColor;
            return this;
        }

        public ExcelWriteSheetEntityBuilder titleRowHeight(int titleRowHeight) {
            this.titleRowHeight = titleRowHeight;
            return this;
        }

        public ExcelWriteSheetEntityBuilder dataRowHeight(int dataRowHeight) {
            this.dataRowHeight = dataRowHeight;
            return this;
        }

        public ExcelWriteSheetEntityBuilder bindType(ExcelColBindType bindType) {
            this.bindType = bindType;
            return this;
        }

        public ExcelWriteSheetEntityBuilder propertyList(List<ExcelCellEntity> propertyList) {
            propertyList.sort(Comparator.comparingInt(ExcelCellEntity::getColIndex));
            this.propertyList = propertyList;
            return this;
        }

        public ExcelWriteSheetEntity build() {
            if (this.rowStriped && null == this.rowStripeColor) {
                this.rowStripeColor = ColorUtils.hexToRgb(ExcelExportConstants.ROW_STRIPE_COLOR);
            }
            return new ExcelWriteSheetEntity(this.sheetName, this.lastColIndex, this.order, this.autoColWidth, this.hasTitle, this.maxRowsPerSheet, this.rowStriped, this.rowStripeColor, this.titleRowHeight, this.dataRowHeight, this.maxColWidth, this.bindType, this.propertyList);
        }

        @Override
        public String toString() {
            return "ExcelWriteSheetEntity.ExcelWriteSheetEntityBuilder(sheetName=" + this.sheetName + ", lastColIndex=" + this.lastColIndex + ", order=" + this.order + ", autoColWidth=" + this.autoColWidth + ", hasTitle=" + this.hasTitle + ", maxRowsPerSheet=" + this.maxRowsPerSheet + ", rowStriped=" + this.rowStriped + ", rowStripeColor=" + this.rowStripeColor + ", titleRowHeight=" + this.titleRowHeight + ", dataRowHeight=" + this.dataRowHeight + ", bindType=" + this.bindType + ", propertyList=" + this.propertyList + ")";
        }
    }
}
