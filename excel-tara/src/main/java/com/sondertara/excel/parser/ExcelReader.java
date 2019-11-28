package com.sondertara.excel.parser;


import com.sondertara.common.util.DateUtil;
import com.sondertara.common.util.NumberUtil;
import com.sondertara.common.util.RegexUtil;
import com.sondertara.common.util.StringUtil;
import com.sondertara.excel.common.Constant;
import com.sondertara.excel.entity.ErrorEntity;
import com.sondertara.excel.entity.ExcelEntity;
import com.sondertara.excel.entity.ExcelPropertyEntity;
import com.sondertara.excel.exception.AllEmptyRowException;
import com.sondertara.excel.exception.ExcelBootException;
import com.sondertara.excel.function.ImportFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author huangxiaohu
 */
public class ExcelReader extends DefaultHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);

    /**
     * 格式化formatter
     */
    private static final DataFormatter FORMATTER = new DataFormatter();
    /**
     * 当前sheet
     */
    private Integer currentSheetIndex = -1;
    /**
     * Excel当前行
     */
    private Integer currentRowIndex = 0;
    /**
     * Excel当前列
     */
    private Integer currentCellIndex = -1;
    /**
     * 行数据索引
     */
    private Integer dataCurrentCellIndex = 0;
    /**
     * 下一个cell数据类型
     */
    private ExcelCellType excelCellType;
    /**
     * 定义前一个元素和当前元素的位置，用来计算其中空的单元格数量，如A6和A8等
     */
    private String previousCellLocation;
    private String currentCellLocation;
    /**
     * 最后一列坐标
     */
    private String endCellLocation;
    /**
     * 共享字符串索引
     */
    private SharedStringsTable mSharedStringsTable;
    /**
     * 当前cell值
     */
    private String currentCellValue;
    /**
     * 是否需要查共享字符串
     */
    private Boolean isNeedSharedStrings = false;
    /**
     * excel映射
     */
    private ExcelEntity excelMapping;
    /**
     * 导入方法
     */
    private ImportFunction importFunction;
    /**
     * 导入pojo
     */
    private Class excelClass;
    /**
     * 一行记录
     */
    private List<String> cellsOnRow = new ArrayList<String>();
    /**
     * 表头
     */
    private List<String> titleRow = new ArrayList<String>();
    /**
     * 开始读取行号
     */
    private Integer beginReadRowIndex;
    /**
     * 是否启用列index对应关系
     */
    private Boolean enableIndex = false;
    /**
     * 单元格格式
     */
    private StylesTable stylesTable;
    /**
     * 单元格number格式化信息
     */
    private short formatIndex;
    private String formatString;


    public ExcelReader(Class entityClass,
                       ExcelEntity excelMapping,
                       ImportFunction importFunction, Boolean enableIndex) {
        this(entityClass, excelMapping, 1, importFunction, enableIndex);
    }

    public ExcelReader(Class entityClass,
                       ExcelEntity excelMapping,
                       Integer beginReadRowIndex,
                       ImportFunction importFunction, Boolean enableIndex) {
        this.excelClass = entityClass;
        this.excelMapping = excelMapping;
        this.beginReadRowIndex = beginReadRowIndex;
        this.importFunction = importFunction;
        this.enableIndex = enableIndex;
    }

    public void process(InputStream in)
            throws IOException, OpenXML4JException, SAXException {

        for (ExcelPropertyEntity entity : excelMapping.getPropertyList()) {
            if (enableIndex && entity.getIndex() < 0) {
                throw new ExcelBootException("Excel导入启动了列对应关系，请标注注解属性对应的index值", entity.getFieldEntity().getName());
            }
        }
        OPCPackage opcPackage = null;
        InputStream sheet = null;
        InputSource sheetSource;
        try {
            opcPackage = OPCPackage.open(in);
            XSSFReader xssfReader = new XSSFReader(opcPackage);
            XMLReader parser = this.fetchSheetParser(xssfReader.getSharedStringsTable());
            this.stylesTable = xssfReader.getStylesTable();

            Iterator<InputStream> sheets = xssfReader.getSheetsData();
            while (sheets.hasNext()) {
                currentRowIndex = 0;
                currentSheetIndex++;
                try {
                    sheet = sheets.next();
                    sheetSource = new InputSource(sheet);
                    try {
                        logger.info("开始读取第{}个Sheet!", currentSheetIndex + 1);
                        parser.parse(sheetSource);
                    } catch (AllEmptyRowException e) {
                        logger.warn(e.getMessage());
                    } catch (Exception e) {
                        throw new ExcelBootException(e, "第{}个Sheet,第{}行,第{}列,系统发生异常! ", currentSheetIndex + 1, currentRowIndex + 1, currentCellIndex);
                    }
                } finally {
                    if (sheet != null) {
                        sheet.close();
                    }
                }
            }
        } finally {
            if (opcPackage != null) {
                opcPackage.close();
            }
        }
    }

    /**
     * 获取sharedStrings.xml文件的XMLReader对象
     *
     * @param sst 字符串索引
     * @return xml解析器
     * @throws SAXException
     */
    private XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        this.mSharedStringsTable = sst;
        parser.setContentHandler(this);
        return parser;
    }

    /**
     * 开始读取一个标签元素
     *
     * @param uri
     * @param localName
     * @param name
     * @param attributes
     */
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) {
        if (Constant.CELL.equals(name)) {
            excelCellType = null;
            String xyzLocation = attributes.getValue(Constant.XYZ_LOCATION);
            previousCellLocation = null == previousCellLocation ? xyzLocation : currentCellLocation;
            currentCellLocation = xyzLocation;
            String cellType = attributes.getValue(Constant.CELL_T_PROPERTY);
            String cellStyleStr = attributes.getValue(Constant.CELL_S_VALUE);
            isNeedSharedStrings = (null != cellType && cellType.equals(Constant.CELL_S_VALUE));
            setCellType(cellType, cellStyleStr);

        }
        currentCellValue = "";
    }

    /**
     * 加载v标签中间的值
     *
     * @param chars
     * @param start
     * @param length
     */
    @Override
    public void characters(char[] chars, int start, int length) {
        currentCellValue = currentCellValue.concat(new String(chars, start, length));
    }

    /**
     * 结束读取一个标签元素
     *
     * @param uri
     * @param localName
     * @param name
     * @throws SAXException
     */
    @Override
    public void endElement(String uri, String localName, String name) {
        if (Constant.CELL.equals(name)) {
            if (isNeedSharedStrings && !StringUtils.isBlank(currentCellValue) && StringUtils.isNumeric(currentCellValue)) {
                int index = Integer.parseInt(currentCellValue);
//                currentCellValue = mSharedStringsTable.getItemAt(index).getString();
                currentCellValue = mSharedStringsTable.getItemAt(index).getString();
            }
            if (!currentCellLocation.equals(previousCellLocation) && currentRowIndex != 0) {
                for (int i = 0; i < countNullCell(currentCellLocation, previousCellLocation); i++) {
                    currentCellIndex = dataCurrentCellIndex + 1;
                    cellsOnRow.add(dataCurrentCellIndex, "");
                    dataCurrentCellIndex++;
                }
            }
            if (currentRowIndex != 0 || !"".equals(currentCellValue.trim())) {
                currentCellIndex = dataCurrentCellIndex + 1;
                String value = this.getCellValue(currentCellValue.trim());
                cellsOnRow.add(dataCurrentCellIndex, value);
                dataCurrentCellIndex++;
            }
        } else if (Constant.ROW.equals(name)) {
            if (currentRowIndex == 0) {
                endCellLocation = currentCellLocation;
                int propertySize = excelMapping.getPropertyList().size();
                if (!enableIndex && cellsOnRow.size() != propertySize) {
                    throw new ExcelBootException("Excel有效列数不等于标注注解的属性数量!Excel列数:{},标注注解的属性数量:{}", cellsOnRow.size(), propertySize);
                }
                if (cellsOnRow.size() < propertySize) {
                    throw new ExcelBootException("Excel有效列数小于标注注解的属性数量!Excel列数:{},标注注解的属性数量:{}", cellsOnRow.size(), propertySize);
                }
                titleRow.addAll(cellsOnRow);
            }
            if (null != endCellLocation) {
                for (int i = 0; i <= countNullCell(endCellLocation, currentCellLocation); i++) {
                    currentCellIndex = dataCurrentCellIndex + 1;
                    cellsOnRow.add(dataCurrentCellIndex, "");
                    dataCurrentCellIndex++;
                }
            }
            try {
                this.assembleData();
            } catch (AllEmptyRowException e) {
                throw e;
            } catch (Exception e) {
                throw new ExcelBootException(e);
            }
            cellsOnRow.clear();
            currentRowIndex++;
            currentCellIndex = -1;
            dataCurrentCellIndex = 0;
            previousCellLocation = null;
            currentCellLocation = null;
            excelCellType = null;
        }

    }

    /**
     * 根据c节点的t属性获取单元格格式
     * 根据c节点的s属性获取单元格样式,去styles.xml文件找相应样式
     *
     * @param cellType xml中单元格格式属性
     */
    private void setCellType(String cellType, String cellStyleStr) {
        if ("b".equals(cellType)) {
            excelCellType = ExcelCellType.BOOL;
        } else if ("e".equals(cellType)) {
            excelCellType = ExcelCellType.ERROR;
        } else if ("inlineStr".equals(cellType)) {
            excelCellType = ExcelCellType.INLINESTR;
        } else if ("s".equals(cellType)) {
            excelCellType = ExcelCellType.SSTINDEX;
        } else if ("str".equals(cellType)) {
            excelCellType = ExcelCellType.FORMULA;
        } else if ("str".equals(cellType)) {
            excelCellType = ExcelCellType.FORMULA;
        } else if (cellStyleStr != null) {
            // It's a number, but almost certainly one
            // with a special style or format
            int styleIndex = Integer.parseInt(cellStyleStr);
            XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
            this.formatIndex = style.getDataFormat();
            this.formatString = style.getDataFormatString();
            excelCellType = ExcelCellType.NUMBER;
            if (this.formatString == null) {
                excelCellType = ExcelCellType.NULL;
                this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
            }

        } else if (null == cellType) {
            excelCellType = ExcelCellType.NULL;

        } else {
            throw new ExcelBootException("Excel单元格格式未设置成文本或者常规!单元格格式:{}", cellType);
        }


    }

    /**
     * 根据数据类型获取数据
     *
     * @param value
     * @return cell值
     */
    private String getCellValue(String value) {
        switch (excelCellType) {
            case INLINESTR:
                return new XSSFRichTextString(value).toString();
            case NUMBER:
                if (this.formatString != null && !StringUtil.isBlank(value)) {
                    return FORMATTER.formatRawCellContents(Double
                            .parseDouble(value), this.formatIndex, this.formatString);
                } else {
                    return String.valueOf(value);
                }
            default:
                return String.valueOf(value);
        }
    }

    private void assembleData() throws Exception {

        if (currentRowIndex >= beginReadRowIndex) {
            List<ExcelPropertyEntity> propertyList = excelMapping.getPropertyList();
            for (int i = 0; i < propertyList.size() - cellsOnRow.size(); i++) {
                cellsOnRow.add(i, "");
            }
            if (isAllEmptyRowData()) {
                throw new AllEmptyRowException("第{}行为空行,第{}个Sheet导入结束!", currentRowIndex + 1, currentSheetIndex + 1);
            }
            Object entity = excelClass.newInstance();
            ErrorEntity errorEntity = null;
            for (int i = 0; i < propertyList.size(); i++) {
                ExcelPropertyEntity property = propertyList.get(i);
                //  dataCurrentCellIndex = i;
                currentCellIndex = enableIndex ? property.getIndex() : i;
                Object cellValue = cellsOnRow.get(currentCellIndex);


                errorEntity = checkCellValue(currentCellIndex, property, cellValue);
                if (null != errorEntity) {
                    break;
                }
                try {
                    cellValue = convertCellValue(property, cellValue);
                } catch (Exception e) {
                    logger.error(" cell value[{}],convertCellValue error...", cellValue, e);
                    errorEntity = buildErrorMsg(currentCellIndex, cellValue, "解析错误");
                    break;
                }
                if (cellValue != null) {
                    Field field = property.getFieldEntity();
                    field.set(entity, cellValue);
                }
            }
            if (null == errorEntity) {
                importFunction.onProcess(currentSheetIndex + 1, currentRowIndex + 1, entity);
            } else {
                importFunction.onError(errorEntity);
            }
        }
    }

    private boolean isAllEmptyRowData() {
        int emptyCellCount = 0;
        for (Object cellData : cellsOnRow) {
            if (StringUtil.isBlank(cellData)) {
                emptyCellCount++;
            }
        }
        return emptyCellCount == cellsOnRow.size();
    }

    private Object convertCellValue(ExcelPropertyEntity mappingProperty, Object cellValue) throws
            ParseException, ExecutionException {
        Class filedClazz = mappingProperty.getFieldEntity().getType();
        if (filedClazz == Date.class) {
            if (!StringUtil.isBlank(cellValue)) {
                try {
                    double parseDouble = Double.parseDouble(cellValue.toString());
                    cellValue = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(parseDouble, TimeZone.getDefault());
                } catch (NumberFormatException e) {
                    cellValue = DateUtil.parse(cellValue.toString());
                }
            } else {
                cellValue = null;
            }
        } else if (filedClazz == String.class) {
            cellValue = StringUtil.convertNullToNull(cellValue);
        } else if (filedClazz == Integer.class) {
            cellValue = NumberUtil.toInt(cellValue);
        } else if (filedClazz == Double.class) {
            cellValue = NumberUtil.toDouble(cellValue);
        } else if (filedClazz == Long.class) {
            cellValue = NumberUtil.toLong(cellValue);
        } else if (filedClazz == Float.class) {
            cellValue = NumberUtil.toFloat(cellValue);
        } else if (filedClazz == BigDecimal.class) {
            cellValue = NumberUtil.toBigDecimalWithScale(cellValue, mappingProperty.getScale(), mappingProperty.getRoundingMode());
        } else if (filedClazz == int.class) {
            cellValue = NumberUtil.toInt(StringUtil.convertToNumber(cellValue, 0));
        } else if (filedClazz == short.class) {
            cellValue = NumberUtil.toShort(StringUtil.convertToNumber(cellValue, (short) 0));
        } else if (filedClazz == double.class) {
            cellValue = NumberUtil.toDouble(StringUtil.convertToNumber(cellValue, 0d));
        } else if (filedClazz == long.class) {
            cellValue = NumberUtil.toLong(StringUtil.convertNullToZero(cellValue), 0L);
        } else if (filedClazz == float.class) {
            cellValue = NumberUtil.toFloat(StringUtil.convertToNumber(cellValue, 0f));
        } else if (filedClazz != String.class) {
            throw new ExcelBootException("不支持的属性类型:{},导入失败!", filedClazz);
        }
        return cellValue;
    }

    private ErrorEntity checkCellValue(Integer cellIndex, ExcelPropertyEntity mappingProperty, Object cellValue) throws
            Exception {
        Boolean required = mappingProperty.getRequired();
        if (null != required && required) {
            if (null == cellValue || StringUtil.isBlank(cellValue)) {
                String validErrorMessage = String.format("第[%s]个Sheet,第[%s]行,第[%s]列必填单元格为空!"
                        , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1);
                return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
            }
            if (Double.MIN_VALUE != mappingProperty.getMin() || Double.MAX_VALUE != mappingProperty.getMax()) {
                final Double v = NumberUtil.toDouble(cellValue);
                if (null == v) {
                    String validErrorMessage = String.format("第[%s]个Sheet,第[%s]行,第[%s]列,单元格转换数值后为空!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                } else if (v > mappingProperty.getMin() || v < mappingProperty.getMin()) {
                    String validErrorMessage = String.format("第[%s]个Sheet,第[%s]行,第[%s]列,单元格值:[%s],数值大小区间校验失败!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);

                }
            }
        }

        String regex = mappingProperty.getRegex();
        if (!StringUtil.isBlank(cellValue) && !StringUtils.isBlank(regex)) {
            boolean matches = RegexUtil.isMatch(regex, cellValue.toString());
            if (!matches) {
                String regularExpMessage = mappingProperty.getRegexMessage();
                String validErrorMessage = String.format("第[%s]个Sheet,第[%s]行,第[%s]列,单元格值:[%s],正则表达式[%s]校验失败!"
                        , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, regularExpMessage);
                return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
            }
        }


        return null;
    }

    private ErrorEntity buildErrorMsg(Integer cellIndex, Object cellValue,
                                      String validErrorMessage) {
        return ErrorEntity.builder()
                .sheetIndex(currentSheetIndex + 1)
                .rowIndex(currentRowIndex + 1)
                .cellIndex(cellIndex + 1)
                .cellValue(StringUtil.convertNullToEmpty(cellValue))
                .columnName(titleRow.get(cellIndex))
                .errorMessage(validErrorMessage)
                .build();
    }

    /**
     * 计算两个单元格之间的单元格数目(同一行)
     *
     * @param refA
     * @param refB
     * @return 单元格差值
     */
    public int countNullCell(String refA, String refB) {
        // excel2007最大行数是1048576，最大列数是16384，最后一列列名是XFD
        String xfdA = refA.replaceAll("\\d+", "");
        String xfdB = refB.replaceAll("\\d+", "");

        xfdA = fillChar(xfdA, 3, '@', true);
        xfdB = fillChar(xfdB, 3, '@', true);

        char[] letterA = xfdA.toCharArray();
        char[] letterB = xfdB.toCharArray();
        int res = (letterA[0] - letterB[0]) * 26 * 26 + (letterA[1] - letterB[1]) * 26 + (letterA[2] - letterB[2]);
        return res - 1;
    }

    private String fillChar(String str, int len, char let, boolean isPre) {
        int lenA = str.length();
        if (lenA < len) {
            if (isPre) {
                StringBuilder strBuilder = new StringBuilder(str);
                for (int i = 0; i < (len - lenA); i++) {
                    strBuilder.insert(0, let);
                }
                str = strBuilder.toString();
            } else {
                StringBuilder strBuilder = new StringBuilder(str);
                for (int i = 0; i < (len - lenA); i++) {
                    strBuilder.append(let);
                }
                str = strBuilder.toString();
            }
        }
        return str;
    }

    /**
     * 单元格中的数据可能的数据类型
     */
    enum ExcelCellType {
        BOOL, ERROR, FORMULA, INLINESTR, NULL, NUMBER, SSTINDEX

    }


}