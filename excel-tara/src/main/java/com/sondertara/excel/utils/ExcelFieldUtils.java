package com.sondertara.excel.utils;

import com.sondertara.common.convert.ConvertUtils;
import com.sondertara.excel.meta.annotation.ExcelExportField;
import com.sondertara.excel.meta.model.ExcelCellDef;
import com.sondertara.excel.meta.model.ExcelRowDef;
import com.sondertara.excel.parser.ExcelDefaultWriterResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

/**
 * @author huangxiaohu
 */
public class ExcelFieldUtils {

    private static final String[] TRY_DATE_FORMAT_LIST = new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss"};

    public static void setCellValue(Cell cell, Object value, Field field, ExcelExportField exportColumn, ExcelDefaultWriterResolver resolver)
            throws IllegalAccessException {
        if (null == value) {
            return;
        }
        if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof String) {
            switch (exportColumn.cellType()) {
                case BLANK:
                    cell.setBlank();
                    break;
                case FORMULA:
                    cell.setCellFormula(value.toString());
                    break;
                default:
                    cell.setCellValue((String) value);
            }

        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        } else if (value instanceof Short) {
            cell.setCellValue((Short) value);
        } else if (value instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) value).doubleValue());
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            throw new UnsupportedOperationException("不支持此数据类型 => [" + field.getType() + "]!");
        }
        if (exportColumn.autoWidth()) {
            resolver.calculateColumnWidth(cell, cell.getColumnIndex());
        }
    }

    public static String getCellValue(ExcelRowDef row, int colIndex) {
        for (ExcelCellDef cell : row.getExcelCells()) {
            if (cell.getColIndex() == colIndex) {
                return cell.getCellValue();
            }
        }
        return "";
    }

    /**
     * 给字段赋值
     *
     * @param field
     * @param o
     * @param cellValue
     * @param dateFormat
     * @throws IllegalAccessException
     */
    public static void setFieldValue(Field field, Object o, Object cellValue, String dateFormat)
            throws IllegalAccessException {

        if (field.getType() == String.class) {
            if (!StringUtils.isBlank(dateFormat)) {
                try{
                    field.set(o, DateUtils.parseDate((String) cellValue, dateFormat));
                } catch (ParseException e) {
                }
            }
        }
        field.set(o, ConvertUtils.convert(field.getType(), cellValue));
    }
}
