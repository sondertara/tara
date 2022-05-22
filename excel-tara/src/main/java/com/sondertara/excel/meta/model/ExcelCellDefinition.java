package com.sondertara.excel.meta.model;

import com.sondertara.excel.ExcelXmlCodecUtils;
import com.sondertara.excel.constants.ExcelConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Excel字段类型定义
 *
 * @author chenzw
 */
public class ExcelCellDefinition implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ExcelCellDefinition.class);
    private static final long serialVersionUID = -1737830792925662139L;

    private Integer sheetIndex;
    private Integer rowIndex;
    private Integer colIndex;
    private String colTitle;
    private String cellValue;
    private ExcelCellType cellType;

    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(final Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(final Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Integer getColIndex() {
        return colIndex;
    }

    public void setColIndex(final Integer colIndex) {
        this.colIndex = colIndex;
    }

    public String getColTitle() {
        return colTitle;
    }

    public void setColTitle(final String colTitle) {
        this.colTitle = colTitle;
    }

    public String getCellValue() {
        return cellValue;
    }

    public void setCellValue(final String cellValue) {
        this.cellValue = cellValue;
    }

    public ExcelCellType getCellType() {
        return cellType;
    }

    public void setCellType(final ExcelCellType cellType) {
        this.cellType = cellType;
    }

    @Override
    public String toString() {
        return "ExcelCell{" + "sheetIndex=" + sheetIndex + ", rowIndex=" + rowIndex + ", colIndex=" + colIndex + ", colTitle='" + colTitle + '\'' + ", cellValue='" + cellValue + '\'' + ", cellType='" + cellType + '\'' + '}';
    }

    /**
     * 单元格格式
     *
     * @author chenzw
     */
    public interface ExcelCellType extends Serializable {

        boolean matches(String name, Attributes attributes);

        String getValue(String value);
    }

    /**
     * 共享字符串格式
     */
    public static class ExcelStringCellType implements ExcelCellType {

        private static final long serialVersionUID = 1368517956940674679L;

        private SharedStrings sst;

        public ExcelStringCellType(final SharedStrings sst) {
            this.sst = sst;
        }

        @Override
        public boolean matches(final String name, final Attributes attributes) {

            if (ExcelConstants.CELL_TAG.equals(name)) {
                // 字符串类型 t="s"
                if (ExcelConstants.CELL_STRING_TYPE.equals(attributes.getValue(ExcelConstants.CELL_TYPE_ATTR))) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("The [{}] matches [{}]", getCellAttributes(attributes), this.getClass().getName());
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getValue(final String value) {
            final int idx = Integer.parseInt(value);
            return new XSSFRichTextString(this.sst.getItemAt(idx).getString()).toString();
        }
    }


    /**
     * 日期格式
     */
    public static class ExcelDateCellType implements ExcelCellType {

        private static final long serialVersionUID = -9216501976104636864L;
        private StylesTable stylesTable;

        public ExcelDateCellType(final StylesTable stylesTable) {
            this.stylesTable = stylesTable;
        }

        @Override
        public boolean matches(final String name, final Attributes attributes) {

            if (ExcelConstants.CELL_TAG.equals(name)) {
                // s不为空
                if (!StringUtils.isBlank(attributes.getValue(ExcelConstants.CELL_STYLE_ATTR))) {
                    final int styleIndex = Integer.parseInt(attributes.getValue(ExcelConstants.CELL_STYLE_ATTR));
                    final XSSFCellStyle cellStyle = this.stylesTable.getStyleAt(styleIndex);
                    final String dataFormatString = cellStyle.getDataFormatString();
                    if (StringUtils.containsAny(dataFormatString, "y", "m", "d", "h", "s", "Y", "M", "D")) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("The [{} - {}] matches [{}]!", getCellAttributes(attributes), dataFormatString, this.getClass().getName());
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public String getValue(final String value) {
            final Date date = DateUtil.getJavaDate(Double.parseDouble(value));
            return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        }
    }

    /**
     * 数值类型
     */
    public static class ExcelNumbericCellType implements ExcelCellType {

        private static final long serialVersionUID = -3254659667807688559L;
        private StylesTable stylesTable;

        public ExcelNumbericCellType(final StylesTable stylesTable) {
            this.stylesTable = stylesTable;
        }

        @Override
        public boolean matches(final String name, final Attributes attributes) {
            if (ExcelConstants.CELL_TAG.equals(name)) {
                // s不为空
                if (!StringUtils.isBlank(attributes.getValue(ExcelConstants.CELL_STYLE_ATTR))) {

                    final String dataFormat = ExcelXmlCodecUtils.getDataFormat(Integer.parseInt(attributes.getValue(ExcelConstants.CELL_STYLE_ATTR)), this.stylesTable);
                    if (StringUtils.containsAny(dataFormat, "#", "General")) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("The [{} - {}] matches [{}]!", getCellAttributes(attributes), dataFormat, this.getClass().getName());
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public String getValue(final String value) {
            return value;
        }
    }

    /**
     * 内联字符串（不使用共享池）
     */
    public static class ExcelInlinStrCellType implements ExcelCellType {

        private static final long serialVersionUID = 9115672114160103097L;

        @Override
        public boolean matches(final String name, final Attributes attributes) {

            if (ExcelConstants.CELL_TAG.equals(name)) {
                // t="inlinStr"
                if (ExcelConstants.CELL_INLINE_STR_TYPE.equals(attributes.getValue(ExcelConstants.CELL_TYPE_ATTR))) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("The [{}] matches [{}]", getCellAttributes(attributes), this.getClass().getName());
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getValue(final String value) {
            final XSSFRichTextString rtsi = new XSSFRichTextString(value);
            return rtsi.toString();
        }
    }


    /**
     * 布尔值类型（1=true; 0=false）
     */
    public static class ExcelBooleanCellType implements ExcelCellType {

        private static final long serialVersionUID = -3049964850218918322L;

        @Override
        public boolean matches(final String name, final Attributes attributes) {
            if (ExcelConstants.CELL_TAG.equals(name)) {
                // t = "b"
                if (ExcelConstants.CELL_BOOLEAN_TYPE.equals(attributes.getValue(ExcelConstants.CELL_TYPE_ATTR))) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("The [{}] matches [{}]", getCellAttributes(attributes), this.getClass().getName());
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getValue(final String value) {
            return value;
        }
    }


    /**
     * 错误类型
     */
    public static class ExcelErrorCellType implements ExcelCellType {

        private static final long serialVersionUID = 9033391964718310293L;

        @Override
        public boolean matches(final String name, final Attributes attributes) {
            if (ExcelConstants.CELL_TAG.equals(name)) {
                // t = "e"
                if (ExcelConstants.CELL_ERROR_TYPE.equals(attributes.getValue(ExcelConstants.CELL_TYPE_ATTR))) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("The [{}] matches [{}]", getCellAttributes(attributes), this.getClass().getName());
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getValue(final String value) {
            return value;
        }
    }


    public static class ExcelNullCellType implements ExcelCellType {

        private static final long serialVersionUID = 1030011032019218508L;

        @Override
        public boolean matches(final String name, final Attributes attributes) {
            if (logger.isDebugEnabled()) {
                logger.debug("The [{}] matches [{}]", getCellAttributes(attributes), this.getClass().getName());
            }
            return true;
        }

        @Override
        public String getValue(final String value) {
            return value;
        }
    }


    private static String getCellAttributes(final Attributes attributes) {
        final int length = attributes.getLength();
        final List<String> attrs = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            attrs.add(attributes.getQName(i) + "=" + attributes.getValue(i));
        }
        return StringUtils.join(attrs);
    }

}
