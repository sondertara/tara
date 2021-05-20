package com.sondertara.common.excel;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sondertara.common.excel.annotation.Export;
import com.sondertara.common.excel.annotation.Import;
import com.sondertara.common.excel.enums.ImportErrorCodeEnum;
import com.sondertara.common.excel.enums.Null;
import com.sondertara.common.excel.model.ImportBaseDTO;
import com.sondertara.common.excel.model.ImportFailureDTO;
import com.sondertara.common.excel.model.ImportResultDTO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author chenxinshi
 */
public class ExcelUtil {

    private static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static Map<String, DateFormat> DATE_FORMAT = Maps.newHashMap();
    private static Map<String, NumberFormat> NUMBER_FORMAT = Maps.newHashMap();

    private static DateFormat getDateFormat(String pattern) {
        if (!DATE_FORMAT.containsKey(pattern)) {
            DATE_FORMAT.put(pattern, new SimpleDateFormat(pattern));
        }
        return DATE_FORMAT.get(pattern);
    }

    private static NumberFormat getNumberFormat(String pattern) {
        if (!NUMBER_FORMAT.containsKey(pattern)) {
            NUMBER_FORMAT.put(pattern, new DecimalFormat(pattern));
        }
        return NUMBER_FORMAT.get(pattern);
    }

    /**
     * 获取workbook对象
     *
     * @param inputStream inputStream
     * @return workbook对象
     */
    public static Workbook getWorkbook(InputStream inputStream) {
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return workbook;
    }

    /**
     * 导出excel
     *
     * @param fileName 文件名（不含扩展名）
     * @param workbook workbook
     * @param response response
     */
    public static void exportWorkbook(String fileName, Workbook workbook, HttpServletResponse response) {
        String fullFileName = fileName + ".xls";
        try {
            fullFileName = URLEncoder.encode(fullFileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment;filename=" + fullFileName);

        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据voClass的Import注解读取并校验转换成dtoList（推荐使用）
     *
     * @param sheet    工作表
     * @param voClass  带Import注解的类
     * @param dtoClass 转换后的类
     * @param <T>      转换后的类（继承ImportBaseDTO）
     * @return dtoList
     */
    public static <T extends ImportBaseDTO> List<T> parse(Sheet sheet, Class voClass, Class<T> dtoClass) {
        List<Map<String, Object>> sheetData = getSheetData(sheet, voClass);
        List<T> result = parseDTO(sheetData, dtoClass, 1);
        validate(result, voClass);
        return result;
    }

    /**
     * 将workbook装成byte数组
     *
     * @param workbook workbook
     * @return 字节数组
     */
    private static byte[] toByteArray(Workbook workbook) {
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 生成导入结果
     *
     * @param parseDTOList   解析转换后的dto列表
     * @param failureDTOList 失败记录的dto列表
     * @param originSheet    原始sheet，为空时不生成失败记录文件
     * @return 导入结果
     */
    public static <T extends ImportBaseDTO> ImportResultDTO getImportResult(List<T> parseDTOList, List<ImportFailureDTO> failureDTOList, Sheet originSheet) {
        ImportResultDTO importResultDTO = ImportResultDTO.getResult(parseDTOList.size(), failureDTOList);

        if (originSheet != null) {
            Map<Integer, String> failMap = failureDTOList.stream().collect(Collectors.toMap(dto -> dto.getRowNum() - 1, ImportFailureDTO::getMessage));
            failMap.put(0, "失败原因");
            Workbook workbook = generateFailedImport(originSheet, failMap);
//            importResultDTO.setUrl(uploadFailedFile(workbook));
        }
        return importResultDTO;
    }

    public static ImportResultDTO getImportResult(List<ImportBaseDTO> parseDTOList, List<ImportFailureDTO> failureDTOList) {
        return getImportResult(parseDTOList, failureDTOList, null);
    }


    /**
     * 获取工作表数据
     *
     * @param sheet sheet对象
     * @return 数据集合，Map的key默认为列下标
     */
    @Deprecated
    public static List<Map<String, Object>> getSheetData(Sheet sheet) {
        return getSheetData(sheet, Maps.newHashMap());
    }

    /**
     * 获取工作表数据
     *
     * @param sheet        sheet对象
     * @param columnsField 列与字段的映射关系map，key：列下标，value：该列对应的字段名，即返回List中Map的key值
     * @return 数据集合
     */
    @Deprecated
    public static List<Map<String, Object>> getSheetData(Sheet sheet, Map<Integer, String> columnsField) {
        if (sheet == null) {
            return null;
        }
        int rowSize = sheet.getLastRowNum() + 1;

        List<Map<String, Object>> dataList = new ArrayList<>(rowSize);
        for (int i = 0; i < rowSize; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                dataList.add(null);
                continue;
            }
            int cellSize = row.getLastCellNum() + 1;
            Map<String, Object> map = new HashMap<>(cellSize);
            for (int j = 0; j < cellSize; j++) {
                Cell cell = row.getCell(j);
                Object cellValue = getCellValue(cell);
                String key;
                if (columnsField != null && columnsField.get(j) != null) {
                    key = columnsField.get(j);
                } else {
                    key = String.valueOf(j);
                }
                map.put(key, cellValue);
            }
            dataList.add(map);
        }
        return dataList;
    }

    /**
     * 根据Import注解获取工作表数据(不带校验，如需校验，请直接使用parse方法)
     *
     * @param sheet 工作表
     * @param cls   带有Import注解字段的类
     * @return 数据集合
     */
    public static List<Map<String, Object>> getSheetData(Sheet sheet, Class cls) {
        if (sheet == null) {
            return null;
        }

        Map<Integer, Field> importFields = getImportFields(cls);

        int rowSize = sheet.getLastRowNum() + 1;
        List<Map<String, Object>> dataList = new ArrayList<>(rowSize);
        for (int i = 0; i < rowSize; i++) {
            Row row = sheet.getRow(i);
            Map<String, Object> map = Maps.newHashMap();
            if (row == null) {
                dataList.add(map);
                continue;
            }
            for (Integer index : importFields.keySet()) {
                Cell cell = row.getCell(index);
                Field field = importFields.get(index);
                String key = field.getName();
                Object cellValue = getCellValue(cell, field);
                if (!isEmpty(cellValue)) {
                    map.put(key, cellValue);
                }
            }
            dataList.add(map);
        }
        return dataList;
    }

    private static boolean isEmpty(Object obj) {
        return obj == null || obj instanceof String && StringUtils.isEmpty((String) obj);
    }

    /**
     * 只做转换，使用JSON.parseObject()将通用数据结构转成dtoList
     *
     * @param dataList 数据列表
     * @param dtoClass 转换后的dtoClass
     * @param startRow 起始行下标，一般从1开始
     * @param <T>      转换后的类（继承ImportBaseDTO）
     * @return dtoList
     */
    private static <T extends ImportBaseDTO> List<T> parseDTO(List<Map<String, Object>> dataList, Class<T> dtoClass, int startRow) {
        List<T> dtoList = Lists.newArrayList();
        for (int i = startRow; i < dataList.size(); i++) {
            Map<String, Object> map = dataList.get(i);
            if (MapUtils.isEmpty(map)) {
                continue;
            }

            T dto = null;
            try {
                dto = JSON.parseObject(JSON.toJSONString(map), dtoClass);
                dto.setSuccess(true);
                dto.setRowNum(i + 1);
            } catch (Exception e) {
                try {
                    dto = dtoClass.newInstance();
                    dto.setSuccess(false);
                    dto.setRowNum(i + 1);
                    dto.setCode(ImportErrorCodeEnum.DATA_TYPE_ERROR.getCode());
                    dto.setMessage(ImportErrorCodeEnum.DATA_TYPE_ERROR.getName());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * 导入字段校验（校验结果会在ImportBaseDTO中）
     *
     * @param dtoList dtoList
     * @param voClass voClass
     * @param <T>     转换后的类（继承ImportBaseDTO）
     */
    private static <T extends ImportBaseDTO> void validate(List<T> dtoList, Class voClass) {
        Map<String, Import> importFiledAnnotations = getImportFiledAnnotations(voClass);
        dtoList.forEach(dto -> importFiledAnnotations.forEach((key, value) -> validate(dto, key, value)));
    }

    /**
     * 校验
     *
     * @param dto        dto
     * @param propName   bean的属性名
     * @param annotation 该属性上的Import注解
     * @param <T>        ImportBaseDTO的子类dto
     */
    private static <T extends ImportBaseDTO> void validate(T dto, String propName, Import annotation) {
        Object value = getBeanProperty(dto, propName);
        boolean isDate = value != null && value instanceof Date;
        boolean isString = value != null && value instanceof String;
        boolean isNumber = value != null && value instanceof Number;
        //必填校验
        if (annotation.required()) {
            if (value == null || isString && "".equals(value)) {
                dto.fail(ImportErrorCodeEnum.NULL.getCode(), String.format("%s必填", annotation.title()));
            }
        }
        if (isDate) {
            Date dateFrom;
            try {
                dateFrom = new SimpleDateFormat("yyyy-MM-dd").parse(annotation.dateFrom());
            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException("dateFrom格式错误");
            }
            if (((Date) value).before(dateFrom)) {
                dto.fail(ImportErrorCodeEnum.OUT_OF_RANGE.getCode(), String.format("日期应大于等于%s", annotation.dateFrom()));
            }
        }
        if (isString) {
            //长度校验
            if (((String) value).length() > annotation.maxLength()) {
                dto.fail(ImportErrorCodeEnum.DATA_TOO_LONG.getCode(), String.format("%s长度不能超过%s", annotation.title(), annotation.maxLength()));
            }
            //枚举校验
            if (annotation.enumClass() != Null.class && StringUtils.isNotEmpty(annotation.enumField())) {
                Class<? extends Enum> aClass = annotation.enumClass();
                if (StringUtils.isNotEmpty((String) value) && !isInEnum(aClass, annotation.enumField(), value)) {
                    dto.fail(ImportErrorCodeEnum.UNKNOWN_DATA.getCode(), String.format("无法识别的%s", annotation.title()));
                }
            }
            //正则表达式
            if (StringUtils.isNotEmpty(annotation.regExp())) {
                if (!((String) value).matches(annotation.regExp())) {
                    dto.fail(ImportErrorCodeEnum.REGEX_NOT_MATCH.getCode(), String.format("%s格式错误", annotation.title()));
                }
            }
        }
        if (isNumber) {
            double dv = ((Number) value).doubleValue();
            //大于
            if (StringUtils.isNotEmpty(annotation.gt()) && !(dv > new Double(annotation.gt()))) {
                dto.fail(ImportErrorCodeEnum.OUT_OF_RANGE.getCode(), String.format("%s必须大于%s", annotation.title(), annotation.gt()));
            }
            //大于等于
            if (StringUtils.isNotEmpty(annotation.gte()) && !(dv >= new Double(annotation.gte()))) {
                dto.fail(ImportErrorCodeEnum.OUT_OF_RANGE.getCode(), String.format("%s必须大于等于%s", annotation.title(), annotation.gte()));
            }
            //小于
            if (StringUtils.isNotEmpty(annotation.lt()) && !(dv < new Double(annotation.lt()))) {
                dto.fail(ImportErrorCodeEnum.OUT_OF_RANGE.getCode(), String.format("%s必须小于%s", annotation.title(), annotation.lt()));
            }
            //小于等于
            if (StringUtils.isNotEmpty(annotation.lte()) && !(dv <= new Double(annotation.lte()))) {
                dto.fail(ImportErrorCodeEnum.OUT_OF_RANGE.getCode(), String.format("%s必须小于等于%s", annotation.title(), annotation.lte()));
            }
        }
    }


    /**
     * 生成workbook
     *
     * @param columnsNum  key: bean的属性名，value: excel列下标
     * @param columnsDesc key: bean的属性名，value: excel列描述
     * @param dataList    数据列表
     * @return workbook
     */
    @Deprecated
    public static Workbook generateWorkbook(Map<String, Integer> columnsNum, Map<String, String> columnsDesc, List dataList) {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");

        Row headRow = sheet.createRow(0);
        for (Map.Entry<String, Integer> entry : columnsNum.entrySet()) {
            Cell cell = headRow.createCell(entry.getValue());
            String colName = columnsDesc.get(entry.getKey());
            setCellValue(cell, colName);
        }

        int i = 1;
        for (Object bean : dataList) {
            Row row = sheet.createRow(i++);
            for (Map.Entry<String, Integer> entry : columnsNum.entrySet()) {
                Cell cell = row.createCell(entry.getValue());
                Object value = getBeanProperty(bean, entry.getKey());
                setCellValue(cell, value);
            }
        }
        return workbook;
    }

    /**
     * 生成workbook（通用方法）
     *
     * @param dataList 数据列表，key:列下标，value：值
     * @return workbook
     */
    public static Workbook generateWorkbook(List<Map<Integer, Object>> dataList) {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");

        int i = 0;
        for (Map<Integer, Object> data : dataList) {
            Row row = sheet.createRow(i++);
            for (Map.Entry<Integer, Object> entry : data.entrySet()) {
                Cell cell = row.createCell(entry.getKey());
                setCellValue(cell, entry.getValue());
            }
        }
        return workbook;
    }

    /**
     * 根据Export注解生成workbook
     *
     * @param dataList 数据列表
     * @param cls      数据列表中bean的class
     * @param <T>      数据列表中bean的类型
     * @return workbook
     */
    public static <T> Workbook generateWorkbook(List<T> dataList, Class<T> cls) {
        List<Map<Integer, Object>> result = Lists.newArrayList();

        Map<Integer, Field> exportFields = getExportFields(cls);
        Map<Integer, Object> exportHeadRow = getExportHeadRow(exportFields);
        result.add(exportHeadRow);

        for (T data : dataList) {
            Map<Integer, Object> map = Maps.newHashMap();
            for (Map.Entry<Integer, Field> entry : exportFields.entrySet()) {
                Object value = getBeanProperty(data, entry.getValue().getName());
                map.put(entry.getKey(), handleCellValue(value, entry.getValue()));
            }
            result.add(map);
        }

        return generateWorkbook(result);
    }

    /**
     * 生成导入模版
     *
     * @param cls 带有Import注解的类
     * @return 导入模版workbook
     */
    public static Workbook generateImportTemplate(Class cls) {
        List<Map<Integer, Object>> result = Lists.newArrayList();

        Map<Integer, Field> importFields = getImportFields(cls);
        Map<Integer, Object> importHeadRow = getImportHeadRow(importFields);
        result.add(importHeadRow);

        return generateWorkbook(result);
    }

    /**
     * 生成失败的导入记录
     *
     * @param originSheet 原始sheet
     * @param remarks     key：失败的行下标，value：行尾要添加的失败原因
     * @return 失败记录的workbook
     */
    private static Workbook generateFailedImport(Sheet originSheet, Map<Integer, String> remarks) {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");

        Map<Integer, String> sort = new TreeMap<>(Comparator.comparingInt(o -> o));
        sort.putAll(remarks);

        int remarkIndex = originSheet.getRow(0).getLastCellNum() + 1;

        int i = 0;
        for (Map.Entry<Integer, String> entry : sort.entrySet()) {
            Row source = originSheet.getRow(entry.getKey());
            Row target = sheet.createRow(i++);
            copyRow(source, target);
            Cell remarkCell = target.createCell(remarkIndex);
            setCellValue(remarkCell, entry.getValue());
        }

        return workbook;
    }

    /**
     * 设置Cell的值（默认为String类型）
     *
     * @param cell  cell
     * @param value value
     */
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellType(CellType.BLANK);
        } else if (value instanceof Date) {
            cell.setCellValue(getDateFormat(DEFAULT_DATE_FORMAT_PATTERN).format((Date) value));
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof RichTextString) {
            cell.setCellValue((RichTextString) value);
        } else {
            cell.setCellValue(String.valueOf(value));
        }
    }

    /**
     * 读取Cell的值
     *
     * @param cell cell
     * @return cell的值
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        Object cellValue;
        switch (cell.getCellType()) {
            case BLANK: {
                cellValue = null;
                break;
            }
            case NUMERIC: {
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = cell.getDateCellValue();
                } else {
                    cellValue = cell.getNumericCellValue();
                }
                break;
            }
            case STRING: {
                cellValue = cell.getStringCellValue().trim();
                break;
            }
            case FORMULA: {
                //获取公式计算后的值
                cellValue = cell.getNumericCellValue();
                //获取公式
//                cellValue = cell.getCellFormula();
                break;
            }
            case BOOLEAN: {
                cellValue = cell.getBooleanCellValue();
                break;
            }
            default:
                cellValue = null;
        }
        return cellValue;
    }

    /**
     * 以String类型读取cell的值
     *
     * @param cell cell
     * @return cell的String值
     */
    private static String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        cell.setCellType(CellType.STRING);
        return cell.getRichStringCellValue().getString();
    }

    /**
     * 根据字段的数据类型对cell的值进行处理
     *
     * @param cell  cell
     * @param field 对应的字段
     * @return 处理后的值
     */
    private static Object getCellValue(Cell cell, Field field) {
        if (field == null) {
            return getCellValue(cell);
        }
        if (field.getType() == String.class) {
            return getCellStringValue(cell);
        }
        return getCellValue(cell);
    }

    /**
     * 根据字段的注解对导出数据值的格式进行处理
     *
     * @param value bean中属性的原始值
     * @param field value对应的字段
     * @return 处理后的值
     */
    private static Object handleCellValue(Object value, Field field) {
        if (field == null) {
            return value;
        }
        Export annotation = field.getAnnotation(Export.class);
        if (value == null) {
            value = annotation.ifNull();
        }
        if (value instanceof BigDecimal && annotation.scale().length == 2) {
            value = ((BigDecimal) value).setScale(annotation.scale()[0], annotation.scale()[1]);
        }
        if (value instanceof Number && StringUtils.isNotEmpty(annotation.numberFormat())) {
            value = getNumberFormat(annotation.numberFormat()).format(value);
        }
        if (value instanceof Date) {
            value = getDateFormat(annotation.dateFormat()).format(value);
        }
        if (value instanceof String && StringUtils.isEmpty((String) value)) {
            value = annotation.ifEmptyString();
        }
        if (value instanceof Iterable && StringUtils.isNotEmpty(annotation.join())) {
            value = StringUtils.join(((Iterable) value).iterator(), annotation.join());
        }
        if (value instanceof Iterable || value instanceof Map) {
            value = JSON.toJSONString(value);
        }
        return value;
    }

    /**
     * 拷贝Row
     *
     * @param sourceRow sourceRow
     * @param targetRow targetRow
     */
    private static void copyRow(Row sourceRow, Row targetRow) {
        for (int i = 0; i < sourceRow.getLastCellNum() + 1; i++) {
            Cell sourceCell = sourceRow.getCell(i);
            if (targetRow.getCell(i) != null) {
                targetRow.removeCell(targetRow.getCell(i));
            }
            Cell targetCell = targetRow.createCell(i);
            setCellValue(targetCell, getCellValue(sourceCell));
        }
    }

    private static Object getBeanProperty(Object bean, String propertyName) {
        Object property = null;
        try {
            property = PropertyUtils.getProperty(bean, propertyName);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return property;
    }

    private static void setBeanProperty(Object bean, String name, Object value) {
        try {
            PropertyUtils.setProperty(bean, name, value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static Map<Integer, Field> getExportFields(Class cls) {
        Field[] fields = cls.getDeclaredFields();
        Map<Integer, Field> map = Maps.newHashMap();
        int i = 0;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Export.class)) {
                Export annotation = field.getAnnotation(Export.class);
                int index = annotation.index() > -1 ? annotation.index() : i;
                if (map.containsKey(index)) {
                    String errorMsg = String.format("Duplicated index %s for field %s and field %s", index, map.get(index).getName(), field.getName());
                    throw new RuntimeException(errorMsg);
                }
                map.put(index, field);
                i++;
            }
        }
        return map;
    }


    private static Map<String, Import> getImportFiledAnnotations(Class cls) {
        Field[] fields = cls.getDeclaredFields();
        Map<String, Import> map = Maps.newHashMap();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Import.class)) {
                map.put(field.getName(), field.getAnnotation(Import.class));
            }
        }
        return map;
    }

    private static Map<Integer, Field> getImportFields(Class cls) {
        Field[] fields = cls.getDeclaredFields();
        Map<Integer, Field> map = Maps.newHashMap();
        int i = 0;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Import.class)) {
                Import annotation = field.getAnnotation(Import.class);
                int index = annotation.index() > -1 ? annotation.index() : i;
                if (map.containsKey(index)) {
                    String errorMsg = String.format("Duplicated index %s for field %s and field %s", index, map.get(index).getName(), field.getName());
                    throw new RuntimeException(errorMsg);
                }
                map.put(index, field);
                i++;
            }
        }
        return map;
    }

    private static Map<Integer, Object> getExportHeadRow(Map<Integer, Field> fields) {
        Map<Integer, Object> map = Maps.newHashMap();

        for (Map.Entry<Integer, Field> entry : fields.entrySet()) {
            Field field = entry.getValue();
            if (field.isAnnotationPresent(Export.class)) {
                map.put(entry.getKey(), field.getAnnotation(Export.class).title());
            }
        }

        return map;
    }

    private static Map<Integer, Object> getImportHeadRow(Map<Integer, Field> fields) {
        Map<Integer, Object> map = Maps.newHashMap();

        for (Map.Entry<Integer, Field> entry : fields.entrySet()) {
            Field field = entry.getValue();
            if (field.isAnnotationPresent(Import.class)) {
                map.put(entry.getKey(), field.getAnnotation(Import.class).title());
            }
        }

        return map;
    }

    private static boolean isInEnum(Class<? extends Enum> enumClass, String fieldName, Object value) {
        Enum[] enumConstants = enumClass.getEnumConstants();
        for (Enum enumConstant : enumConstants) {
            Class<? extends Enum> aClass = enumConstant.getClass();
            try {
                Field name = aClass.getDeclaredField(fieldName);
                name.setAccessible(true);
                if (name.get(enumConstant).equals(value)) {
                    return true;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
