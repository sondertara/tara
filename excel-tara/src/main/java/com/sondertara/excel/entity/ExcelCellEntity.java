//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.sondertara.excel.entity;

import com.sondertara.common.collection.Lists;
import com.sondertara.excel.common.constants.ExcelExportConstants;
import com.sondertara.excel.meta.annotation.ExcelDataFormat;
import com.sondertara.excel.meta.style.CellStyleBuilder;
import com.sondertara.excel.meta.style.DefaultDataCellStyleBuilder;
import com.sondertara.excel.meta.style.DefaultTitleCellStyleBuilder;
import com.sondertara.excel.support.cache.CellStyleCache;
import com.sondertara.excel.support.validator.ValueRangeValidator;
import lombok.Getter;
import org.apache.poi.ss.usermodel.CellType;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author huangxiaohu
 */
@Getter
public class ExcelCellEntity {
    @Nullable
    private Field fieldEntity;

    private final String fieldName;
    private List<String> titles;
    private Integer colWidth;
    /**
     * 列索引 zero-based
     */
    private Integer colIndex;
    private boolean authWith;
    private boolean autoMerge;
    private CellType cellType;
    private String defaultValue;

    @Nullable
    private ExcelDataFormat dateFormat;
    private CellStyleBuilder dataStyle;
    private CellStyleBuilder headStyle;
    private String regex;
    private String regexMessage;
    private Integer scale;
    private RoundingMode roundingMode;
    private Boolean required;
    private ValueRangeValidator rangeValidator;
    private Class<?> dataCellStyleBuilder;

    public String getTitle(int index) {
        if (index < 0 || index >= titles.size()) {
            return null;
        }
        return titles.get(index);
    }

    ExcelCellEntity(@Nullable Field fieldEntity, String fieldName, String[] columnName, Integer colWidth, Integer colIndex, boolean authWith, boolean autoMerge, CellType cellType, String defaultValue, ExcelDataFormat dateFormat, CellStyleBuilder dataStyle, CellStyleBuilder headStyle, String regex, String regexMessage, Integer scale, RoundingMode roundingMode, Boolean required, ValueRangeValidator rangeValidator, Class<?> dataCellStyleBuilder) {
        this.fieldEntity = fieldEntity;
        this.titles = Lists.newArrayList(columnName);
        this.colWidth = colWidth;
        this.colIndex = colIndex;
        this.authWith = authWith;
        this.autoMerge = autoMerge;
        this.cellType = cellType;
        this.defaultValue = defaultValue;
        this.dateFormat = dateFormat;
        this.dataStyle = dataStyle;
        this.headStyle = headStyle;
        this.regex = regex;
        this.regexMessage = regexMessage;
        this.scale = scale;
        this.roundingMode = roundingMode;
        this.required = required;
        this.rangeValidator = rangeValidator;
        this.dataCellStyleBuilder = dataCellStyleBuilder;
        this.fieldName = null == fieldEntity ? fieldName : fieldEntity.getName();
    }

    public static ExcelCellEntityBuilder builder() {
        return new ExcelCellEntityBuilder();
    }

    public void setFieldEntity(Field fieldEntity) {
        this.fieldEntity = fieldEntity;
    }


    public void setColWidth(Integer colWidth) {
        this.colWidth = colWidth;
    }

    public void setColIndex(Integer index) {
        this.colIndex = index;
    }

    public void setAuthWith(boolean authWith) {
        this.authWith = authWith;
    }

    public void setAutoMerge(boolean autoMerge) {
        this.autoMerge = autoMerge;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setDateFormat(ExcelDataFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setDataStyle(CellStyleBuilder dataStyle) {
        this.dataStyle = dataStyle;
    }

    public void setHeadStyle(CellStyleBuilder headStyle) {
        this.headStyle = headStyle;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public void setRegexMessage(String regexMessage) {
        this.regexMessage = regexMessage;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public void setRoundingMode(RoundingMode roundingMode) {
        this.roundingMode = roundingMode;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public void setRangeValidator(ValueRangeValidator rangeValidator) {
        this.rangeValidator = rangeValidator;
    }

    public void setDataCellStyleBuilder(Class<?> dataCellStyleBuilder) {
        this.dataCellStyleBuilder = dataCellStyleBuilder;
    }

    public static class ExcelCellEntityBuilder {
        private Field fieldEntity;
        private String[] columnName;
        private int colWidth = ExcelExportConstants.DEFAULT_COL_WIDTH;
        private int colIndex;
        private boolean authWith;
        private boolean autoMerge;
        private CellType cellType = CellType.STRING;
        private String defaultValue;
        private ExcelDataFormat dateFormat;
        private CellStyleBuilder dataStyle = CellStyleCache.getInstance().getCellStyleInstance(DefaultDataCellStyleBuilder.class);
        private CellStyleBuilder headStyle = CellStyleCache.getInstance().getCellStyleInstance(DefaultTitleCellStyleBuilder.class);
        private String regex;
        private String regexMessage;
        private Integer scale;
        private RoundingMode roundingMode;
        private Boolean required;
        private ValueRangeValidator rangeValidator;
        private Class<?> dataCellStyleBuilder;
        private String fieldName;

        ExcelCellEntityBuilder() {
        }

        public ExcelCellEntityBuilder titles(String[] headNames) {
            this.columnName = headNames;
            return this;
        }

        public ExcelCellEntityBuilder fieldEntity(Field fieldEntity) {
            this.fieldEntity = fieldEntity;
            this.fieldName = fieldEntity.getName();
            return this;
        }

        public ExcelCellEntityBuilder fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }


        public ExcelCellEntityBuilder colWidth(Integer colWidth) {
            this.colWidth = colWidth;
            return this;
        }

        public ExcelCellEntityBuilder index(Integer index) {
            this.colIndex = index;
            return this;
        }

        public ExcelCellEntityBuilder authWith(boolean authWith) {
            this.authWith = authWith;
            return this;
        }

        public ExcelCellEntityBuilder autoMerge(boolean autoMerge) {
            this.autoMerge = autoMerge;
            return this;
        }

        public ExcelCellEntityBuilder cellType(CellType cellType) {
            this.cellType = cellType;
            return this;
        }

        public ExcelCellEntityBuilder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public ExcelCellEntityBuilder dateFormat(ExcelDataFormat dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        public ExcelCellEntityBuilder dataStyle(CellStyleBuilder dataStyle) {
            this.dataStyle = dataStyle;
            return this;
        }

        public ExcelCellEntityBuilder headStyle(CellStyleBuilder headStyle) {
            this.headStyle = headStyle;
            return this;
        }

        public ExcelCellEntityBuilder regex(String regex) {
            this.regex = regex;
            return this;
        }

        public ExcelCellEntityBuilder regexMessage(String regexMessage) {
            this.regexMessage = regexMessage;
            return this;
        }

        public ExcelCellEntityBuilder scale(Integer scale) {
            this.scale = scale;
            return this;
        }

        public ExcelCellEntityBuilder roundingMode(RoundingMode roundingMode) {
            this.roundingMode = roundingMode;
            return this;
        }

        public ExcelCellEntityBuilder required(Boolean required) {
            this.required = required;
            return this;
        }

        public ExcelCellEntityBuilder rangeValidator(ValueRangeValidator rangeValidator) {
            this.rangeValidator = rangeValidator;
            return this;
        }

        public ExcelCellEntityBuilder dataCellStyleBuilder(Class<?> dataCellStyleBuilder) {
            this.dataCellStyleBuilder = dataCellStyleBuilder;
            return this;
        }

        public ExcelCellEntity build() {
            return new ExcelCellEntity(this.fieldEntity, this.fieldName, this.columnName, this.colWidth, this.colIndex, this.authWith, this.autoMerge, this.cellType, this.defaultValue, this.dateFormat, this.dataStyle, this.headStyle, this.regex, this.regexMessage, this.scale, this.roundingMode, this.required, this.rangeValidator, this.dataCellStyleBuilder);
        }

        @Override
        public String toString() {
            return "ExcelCellEntity.ExcelCellEntityBuilder(fieldEntity=" + this.fieldEntity + ", columnName=" + this.columnName + ", colWidth=" + this.colWidth + ", index=" + this.colIndex + ", authWith=" + this.authWith + ", autoMerge=" + this.autoMerge + ", cellType=" + this.cellType + ", defaultValue=" + this.defaultValue + ", dateFormat=" + this.dateFormat + ", dataStyle=" + this.dataStyle + ", headStyle=" + this.headStyle + ", regex=" + this.regex + ", regexMessage=" + this.regexMessage + ", scale=" + this.scale + ", roundingMode=" + this.roundingMode + ", required=" + this.required + ", rangeValidator=" + this.rangeValidator + ", dataCellStyleBuilder=" + this.dataCellStyleBuilder + ")";
        }
    }
}
