package com.sondertara.excel.utils;

import com.sondertara.common.convert.ConvertUtils;
import com.sondertara.common.datetime.LocalDateTimeUtils;
import com.sondertara.common.math.NumberUtils;
import com.sondertara.common.text.StringUtils;
import com.sondertara.excel.common.constants.Constants;
import com.sondertara.excel.entity.ExcelCellEntity;
import com.sondertara.excel.meta.annotation.ExcelDataFormat;
import com.sondertara.excel.meta.model.ExcelCellDef;
import com.sondertara.excel.meta.model.ExcelRowDef;
import com.sondertara.excel.resolver.ExcelDefaultWriterResolver;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author huangxiaohu
 */
public class ExcelFieldUtils {

    private static final long DAY_MILLISECONDS = 86_400_000L;

    private static final String[] TRY_DATE_FORMAT_LIST = new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss"};

    public static void setCellValue(Cell cell, Object value, Field field, ExcelCellEntity cellEntity, ExcelDefaultWriterResolver resolver)
            throws IllegalAccessException {
        if (null == value) {
            return;
        }
        if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof String) {
            switch (cellEntity.getCellType()) {
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
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            throw new UnsupportedOperationException("不支持此数据类型 => [" + field.getType() + "]!");
        }
        if (Constants.DEFAULT_COL_WIDTH != cellEntity.getColWidth()) {
            resolver.addColumnWidth(cell.getColumnIndex(), cellEntity.getColWidth());
        }
        if (cellEntity.isAuthWith()) {
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
    public static void setFieldValue(Field field, Object o, Object cellValue, ExcelDataFormat dateFormat, boolean date1904) throws IllegalAccessException {
        Object targetValue = null;
        if (field.getType() == String.class) {
            String value = dateFormat.value();
            if (StringUtils.isNotBlank(value)) {
                //时间类型
                if (0 == dateFormat.type()) {
                    //数字类型，转换时间
                    if (NumberUtils.isCreatable(cellValue.toString())) {
                        double doubleValue = new BigDecimal(cellValue.toString()).doubleValue();
                        LocalDateTime localDateTime;
                        if (DateUtil.isValidExcelDate(doubleValue)) {
                            localDateTime = DateUtil.getLocalDateTime(doubleValue, date1904);
                        } else {
                            localDateTime = convertToDate(doubleValue, date1904);
                        }
                        targetValue = LocalDateTimeUtils.format(localDateTime, value);
                    } else {
                        //其他类型尝试转换
                        Date date = LocalDateTimeUtils.parse(cellValue.toString());
                        if (null != date) {
                            targetValue = LocalDateTimeUtils.format(date, value);
                        }
                    }

                }
            }
        } else if (Date.class.isAssignableFrom(field.getType())) {
            if (NumberUtils.isCreatable(cellValue.toString())) {
                double doubleValue = new BigDecimal(cellValue.toString()).doubleValue();
                LocalDateTime date = convertToDate(doubleValue, date1904);
                targetValue = LocalDateTimeUtils.date(date);
            } else {
                targetValue = LocalDateTimeUtils.parse(cellValue.toString());
            }
        } else if (LocalDateTime.class.isAssignableFrom(field.getType())) {
            if (NumberUtils.isCreatable(cellValue.toString())) {
                double doubleValue = new BigDecimal(cellValue.toString()).doubleValue();
                targetValue = convertToDate(doubleValue, date1904);
            } else {
                targetValue = LocalDateTimeUtils.parseLocalDateTime(cellValue.toString());
            }
        }
        if (null == targetValue) {
            targetValue = ConvertUtils.convert(field.getType(), cellValue);
        }
        field.set(o, targetValue);
    }

    public static LocalDateTime convertToDate(double value, boolean date1904) {
        int wholeDays = (int) Math.floor(value);
        long millisecondsInDay = (long) (((value - wholeDays) * DAY_MILLISECONDS) + 0.5D);

        int startYear = 1900;
        int dayAdjust = -1; // Excel thinks 2/29/1900 is a valid date, which it isn't
        if (date1904) {
            startYear = 1904;
            dayAdjust = 1; // 1904 date windowing uses 1/2/1904 as the first day
        } else if (wholeDays < 61) {
            // Date is prior to 3/1/1900, so adjust because Excel thinks 2/29/1900 exists
            // If Excel date == 2/29/1900, will become 3/1/1900 in Java representation
            dayAdjust = 0;
        }
        LocalDate localDate = LocalDate.of(startYear, 1, 1).plusDays((long) wholeDays + dayAdjust - 1);
        LocalTime localTime = LocalTime.ofNanoOfDay(millisecondsInDay * 1_000_000);
        return LocalDateTime.of(localDate, localTime);
    }
}
