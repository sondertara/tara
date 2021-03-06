package com.sondertara.excel.parser;


import com.sondertara.common.util.DateUtil;
import com.sondertara.common.util.NumberUtil;
import com.sondertara.common.util.RegexUtil;
import com.sondertara.common.util.StringUtil;
import com.sondertara.excel.annotation.ImportField;
import com.sondertara.excel.common.Constant;
import com.sondertara.excel.entity.ErrorEntity;
import com.sondertara.excel.entity.ExcelEntity;
import com.sondertara.excel.entity.ExcelPropertyEntity;
import com.sondertara.excel.enums.FieldRangeType;
import com.sondertara.excel.exception.AllEmptyRowException;
import com.sondertara.excel.exception.ExcelTaraException;
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
     * formatter
     */
    private static final DataFormatter FORMATTER = new DataFormatter();
    /**
     * current sheet
     */
    private Integer currentSheetIndex = -1;
    /**
     * row of sheet
     */
    private Integer currentRowIndex = 0;
    /**
     * column of sheet
     */
    private Integer currentCellIndex = -1;
    /**
     * data list index of a row
     */
    private Integer dataCurrentCellIndex = 0;
    /**
     * next cell type
     */
    private ExcelCellType excelCellType;
    /**
     * the pre cell and current cell ,to calculate the distance like A6 and A8
     */
    private String previousCellLocation;
    private String currentCellLocation;
    /**
     * the last cell location
     */
    private String endCellLocation;
    /**
     * shared strings table
     */
    private SharedStringsTable mSharedStringsTable;
    /**
     * current cell value
     */
    private String currentCellValue;
    /**
     * is need to read shared strings
     */
    private Boolean isNeedSharedStrings = false;
    /**
     * excel entity via annotation
     */
    private ExcelEntity excelMapping;
    /**
     * import function
     */
    private ImportFunction importFunction;
    /**
     * class which will parse data
     */
    private Class excelClass;
    /**
     * data of one row
     */
    private List<String> cellsOnRow = new ArrayList<String>();
    /**
     * data of the head
     */
    private List<String> titleRow = new ArrayList<String>();
    /**
     * the row  index begin to read.( head is 0)
     */
    private Integer beginReadRowIndex;
    /**
     * is enable the index  mapping relation
     * <p>
     * if true the index value  is the column in excel,else the field and column one-to-one
     *
     * @see ImportField#index()
     * </p>
     */
    private Boolean enableIndex = false;
    /**
     * style in a cell
     */
    private StylesTable stylesTable;
    /**
     * cell formatter
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
                throw new ExcelTaraException("Excel enable the index mapping relation .please set [index] filed via @ImportField", entity.getFieldEntity().getName());
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
                        logger.info("start read the Sheet[{}]....", currentSheetIndex + 1);
                        parser.parse(sheetSource);
                    } catch (AllEmptyRowException e) {
                        logger.warn(e.getMessage());
                    } catch (Exception e) {
                        throw new ExcelTaraException(e, "Sheet[{}],row[{}],column[{}],parse error... ", currentSheetIndex + 1, currentRowIndex + 1, currentCellIndex);
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
     * get XMLReader instance of sharedStrings.xml
     *
     * @param sst SharedStringsTable
     * @return XMLReader
     * @throws SAXException exception
     */
    private XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        this.mSharedStringsTable = sst;
        parser.setContentHandler(this);
        return parser;
    }

    /**
     * read  start the first xml element
     *
     * @param uri        The Namespace URI
     * @param localName  he local name (without prefix)
     * @param name       The qualified name (with prefix)
     * @param attributes The attributes attached to the element
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
     * get the value between tag 'v'
     *
     * @param chars  The characters
     * @param start  start
     * @param length len
     */
    @Override
    public void characters(char[] chars, int start, int length) {
        currentCellValue = currentCellValue.concat(new String(chars, start, length));
    }

    /**
     * read end the xml element
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
                    throw new ExcelTaraException("Excel qualified columns size not equal the  fields size via @ImportFiled in pojo.Excel columns size [{}],via import annotation size[{}]...you can enable index mapping relation ", cellsOnRow.size(), propertySize);
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
                throw new ExcelTaraException(e);
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
     * set cell type by 'c' node and 't' node
     *
     * @param cellType     cell type
     * @param cellStyleStr cell style
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
            throw new ExcelTaraException("Excel cell not Text or General! the cell type is:{},please fix it first", cellType);
        }


    }

    /**
     * get cell value
     *
     * @param value
     * @return cell value
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

    /**
     * generate one row data list
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void assembleData() throws Exception {

        if (currentRowIndex >= beginReadRowIndex) {
            List<ExcelPropertyEntity> propertyList = excelMapping.getPropertyList();
            for (int i = 0; i < propertyList.size() - cellsOnRow.size(); i++) {
                cellsOnRow.add(i, "");
            }
            if (isAllEmptyRowData()) {
                throw new AllEmptyRowException("The row[{}] is all empty,the sheet[{}] import exit!", currentRowIndex + 1, currentSheetIndex + 1);
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
                    logger.error(" cell value[{}],convert cell value error...", cellValue, e);
                    errorEntity = buildErrorMsg(currentCellIndex, cellValue, "Parse error");
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

    /**
     * all cell is empty of one row
     *
     * @return is empty row
     */
    private boolean isAllEmptyRowData() {
        int emptyCellCount = 0;
        for (Object cellData : cellsOnRow) {
            if (StringUtil.isBlank(cellData)) {
                emptyCellCount++;
            }
        }
        return emptyCellCount == cellsOnRow.size();
    }

    /**
     * parse cell value to pojo via {@link ImportField}
     *
     * @param mappingProperty pojo filed attribute in {@link ImportField}
     * @param cellValue       cell value
     * @return the qualified value
     * @throws ParseException
     * @throws ExecutionException
     */
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
            throw new ExcelTaraException("The field type[{}] not support,import error", filedClazz);
        }
        return cellValue;
    }

    /**
     * check the cell value via {@link ImportField}
     *
     * @param cellIndex       cell index
     * @param mappingProperty pojo field attribute
     * @param cellValue       cell value
     * @return error entity if  pass validate  will return null
     * @throws Exception
     */
    private ErrorEntity checkCellValue(Integer cellIndex, ExcelPropertyEntity mappingProperty, Object cellValue) throws
            Exception {
        // is required
        Boolean required = mappingProperty.getRequired();
        if (null != required && required) {
            if (null == cellValue || StringUtil.isBlank(cellValue)) {

                String validErrorMessage = String.format("The sheet[{}],row[{}],column[{}] is required,but now is empty!"
                        , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1);
                return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
            }
        }
        //regex
        String regex = mappingProperty.getRegex();
        if (!StringUtil.isBlank(cellValue) && !StringUtils.isBlank(regex)) {
            boolean matches = RegexUtil.isMatch(regex, cellValue.toString());
            if (!matches) {
                String regularExpMessage = mappingProperty.getRegexMessage();
                String validErrorMessage = String.format("The sheet[{}],row[{}],column[{}],cell value[%s] not pass the regex validation!"
                        , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, regularExpMessage);
                return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
            }
        }
        // value range
        Class filedClazz = mappingProperty.getFieldEntity().getType();
        String[] range = mappingProperty.getRange();
        if (range.length == 0) {
            return null;
        } else if (StringUtil.isEmpty(range[0]) && StringUtil.isEmpty(range[1])) {
            return null;
        } else if (range.length != 2) {
            throw new Exception("the ImportFiled annotation attribute[range] should a string[] with two elements!");
        }
        String simpleName = filedClazz.getSimpleName();

        if ("Date".equals(simpleName)) {
            return checkRangeDate(cellIndex, filedClazz, range, cellValue, mappingProperty.getRangeType());
        } else if ("Integer".equals(simpleName)
                || "double".equals(simpleName.toLowerCase())
                || "BigDecimal".equals(simpleName)
                || "float".equals(simpleName.toLowerCase())
                || "int".equals(simpleName)
                || "short".equals(simpleName.toLowerCase())
                || "long".equals(simpleName.toLowerCase())) {
            return checkRangeNumber(cellIndex, filedClazz, range, cellValue, mappingProperty.getRangeType());
        }
        return null;
    }

    /**
     * build error entity
     *
     * @param cellIndex         cell index in excel
     * @param cellValue         value
     * @param validErrorMessage error msg
     * @return error entity
     */
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
     * calculate distance of the two cell in one row
     *
     * @param refA
     * @param refB
     * @return the distance
     */
    public int countNullCell(String refA, String refB) {
        // excel2007 max row is 1048576，max column is 16384，the last column name is 'XFD'
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
     * if the field is date check date range
     *
     * @param cellIndex
     * @param filedClazz
     * @param range
     * @param cellValue
     * @param rangeType
     * @return error entity
     * @throws Exception
     */
    private ErrorEntity checkRangeDate(Integer cellIndex, Class filedClazz, String[] range, Object cellValue, FieldRangeType rangeType) throws Exception {
        final Date min = DateUtil.parse(range[0]);
        final Date max = DateUtil.parse(range[1]);
        if (null == min && null == max) {
            throw new Exception("The ImportFiled annotation attribute[range] value must be date string[]");
        } else if (null != max && null != min && max.getTime() <= min.getTime()) {
            throw new Exception(String.format("The ImportFiled annotation attribute[range] value %s is illegal!", Arrays.toString(range)))
                    ;
        }
        Date v = null;
        try {
            v = DateUtil.parse(String.valueOf(cellValue));
        } catch (ExecutionException e) {
            String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] must can be parsed to a date!"
                    , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0], range[1]);
            return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
        }
        if (null == v) {
            String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s]  is empty after converted!"
                    , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1);
            return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
        }
        switch (rangeType) {
            case RANGE_CLOSE:
                if (null == max && v.getTime() < min.getTime()) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the date range '[%s,]' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                } else if (null == min && v.getTime() > max.getTime()) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the date range '[,%s]' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                } else if (v.getTime() > max.getTime() || v.getTime() < min.getTime()) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the date range '[%s,%s]' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0], range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                }
                break;
            case RANGE_OPEN:
                if (null == max && v.getTime() <= min.getTime()) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '(%s,)' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                } else if (null == min && v.getTime() >= max.getTime()) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '(,%s)' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                } else if (v.getTime() >= max.getTime() || v.getTime() <= min.getTime()) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '(%s,%s)' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0], range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                }
                break;
            case RANGE_LEFT_OPEN:
                if (null == max && v.getTime() <= min.getTime()) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '(%s,]' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                } else if (null == min && v.getTime() > max.getTime()) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '(,%s]' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                } else if (v.getTime() > max.getTime() || v.getTime() <= min.getTime()) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '(%s,%s]' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0], range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                }
                break;
            case RANGE_RIGHT_OPEN:
                if (null == max && v.getTime() < min.getTime()) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '[%s,)' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                } else if (null == min && v.getTime() >= max.getTime()) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '[,%s)' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                } else if (v.getTime() >= max.getTime() || v.getTime() < min.getTime()) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '[%s,%s)' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0], range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                }
                break;
            default:
        }
        return null;

    }

    /**
     * if the field is number check range
     *
     * @param cellIndex
     * @param filedClazz
     * @param range
     * @param cellValue
     * @param rangeType
     * @return error entity
     * @throws Exception
     */
    private ErrorEntity checkRangeNumber(Integer cellIndex, Class filedClazz, String[] range, Object cellValue, FieldRangeType rangeType) throws Exception {

        Double min = null;
        Double max = null;
        try {
            min = NumberUtil.toDouble(range[0]);
            max = NumberUtil.toDouble(range[1]);
        } catch (Exception e) {
            throw new Exception("the ImportFiled annotation attribute[range] value must be number string[]");
        }
        Double v = null;
        try {
            v = NumberUtil.toDouble(cellValue);
        } catch (Exception e) {
        }
        if (null == min && null == max) {
            throw new Exception("the ImportFiled annotation attribute[range] value must be number string[]");
        } else if (null != max && null != min && max <= min) {
            throw new Exception(String.format("The ImportFiled annotation attribute[range] value %s is illegal!", Arrays.toString(range)));
        }
        if (null == v) {
            String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s]  is empty after converted!"
                    , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1);
            return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
        }
        switch (rangeType) {
            case RANGE_CLOSE:
                if (null == max && v < min) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '[%s,]' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);

                } else if (null == min && v > max) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '[,%s]' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                } else if (v > max || v < min) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '[%s,%s]' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0], range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                }
                break;
            case RANGE_OPEN:
                if (null == max && v <= min) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '(%s,]' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);

                } else if (null == min && v >= max) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '[,%s)' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                } else if (v >= max || v <= min) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '(%s,%s)' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0], range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                }
                break;
            case RANGE_LEFT_OPEN:
                if (null == max && v <= min) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '(%s,]' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);

                } else if (null == min && v > min) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '(,%s]' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);

                } else if (v > max || v <= min) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '(%s,%s]' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0], range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);

                }
                break;
            case RANGE_RIGHT_OPEN:
                if (null == max && v < min) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '[%s,)' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                } else if (null == min && v >= max) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '[,%s)' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                } else if (v >= max || v < min) {
                    String validErrorMessage = String.format("The sheet[%s],row[%s],column[%s],cell value[%s] not pass the number range '[%s,%s)' validation!"
                            , currentSheetIndex + 1, currentRowIndex + 1, cellIndex + 1, cellValue, range[0], range[1]);
                    return buildErrorMsg(cellIndex, cellValue, validErrorMessage);
                }
                break;
            default:
        }
        return null;
    }


    /**
     * cell type
     */
    enum ExcelCellType {
        BOOL, ERROR, FORMULA, INLINESTR, NULL, NUMBER, SSTINDEX

    }


}