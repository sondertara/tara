package com.sondertara.excel.analysis;

import com.sondertara.excel.constants.ExcelConstants;
import com.sondertara.excel.meta.celltype.ExcelBooleanCellType;
import com.sondertara.excel.meta.celltype.ExcelCellType;
import com.sondertara.excel.meta.celltype.ExcelDateCellType;
import com.sondertara.excel.meta.celltype.ExcelErrorCellType;
import com.sondertara.excel.meta.celltype.ExcelInlineStrCellType;
import com.sondertara.excel.meta.celltype.ExcelNullCellType;
import com.sondertara.excel.meta.celltype.ExcelNumberCellType;
import com.sondertara.excel.meta.celltype.ExcelStringCellType;
import com.sondertara.excel.meta.model.TaraCell;
import com.sondertara.excel.meta.model.TaraRow;
import com.sondertara.excel.meta.model.TaraSheet;
import com.sondertara.excel.meta.model.TaraWorkbook;
import com.sondertara.excel.utils.ExcelXmlCodecUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author huangxiaohu
 */

@Slf4j
public class RawXlsxAnalysisHandler extends DefaultHandler implements LifecycleSupport {
    private StylesTable stylesTable;
    private SharedStrings sst;
    private TaraWorkbook workbook;
    private int totalRow = 0;
    private String tagValue;

    private int rowIndex;

    private Map<Integer, String> sheetNames = new LinkedHashMap<>();
    private TaraRow curExcelRow;
    private TaraCell curExcelCell;

    private TaraSheet curExcelSheet;

    private AtomicInteger sheetIndex = new AtomicInteger(0);
    private List<ExcelCellType> excelCellTypes = new ArrayList<>();

    public RawXlsxAnalysisHandler(final StylesTable stylesTable, final SharedStrings sst, TaraWorkbook workbook) {
        this.sst = sst;
        this.stylesTable = stylesTable;
        this.workbook = workbook;
        registerExcelCellTypes();
    }

    /**
     * 注册单元格类型列表
     */
    private void registerExcelCellTypes() {
        this.excelCellTypes.add(new ExcelStringCellType(this.sst));
        this.excelCellTypes.add(new ExcelNumberCellType(this.stylesTable));
        this.excelCellTypes.add(new ExcelDateCellType(this.stylesTable));
        this.excelCellTypes.add(new ExcelBooleanCellType());
        this.excelCellTypes.add(new ExcelInlineStrCellType());
        this.excelCellTypes.add(new ExcelErrorCellType());
        this.excelCellTypes.add(new ExcelNullCellType());
    }

    @Override
    public void startElement(final String uri, final String localName, final String name, final Attributes attributes)
            throws SAXException {

        if ("workbookView".equals(localName)) {
            String activeTab = attributes.getValue("activeTab");
            if (activeTab != null) {
                this.workbook.setActiveTab(Integer.parseInt(activeTab));
            }
        }
        if (ExcelConstants.SHEET.equals(localName)) {
            String sheetName = attributes.getValue("name");
            sheetNames.put(sheetIndex.getAndIncrement(), sheetName);
        }
        // 总行数
        if (ExcelConstants.DIMENSION_TAG.equals(name)) {
            final String refAttr = attributes.getValue(ExcelConstants.DIMENSION_REF_ATTR);
            this.totalRow = ExcelXmlCodecUtils.getTotalRow(refAttr);
            return;
        }

        // 行
        if (ExcelConstants.ROW_TAG.equals(name)) {
            this.rowIndex = Integer.parseInt(attributes.getValue(ExcelConstants.ROW_INDEX_ATTR));
            this.curExcelRow = new TaraRow(rowIndex, rowIndex);
        }

        // 单元格
        if (ExcelConstants.CELL_TAG.equals(name)) {
            final String abcColIndex = attributes.getValue(ExcelConstants.CELL_ABC_INDEX_ATTR);
            this.curExcelCell = new TaraCell(new CellAddress(rowIndex, ExcelXmlCodecUtils.getColIndex(abcColIndex)));
            // 设置单元格处理器
            for (final ExcelCellType excelCellType : this.excelCellTypes) {
                if (excelCellType.matches(name, attributes)) {
                    this.curExcelCell.setType(excelCellType);
                    break;
                }
            }
        }
        this.tagValue = "";

    }

    @Override
    public void endElement(final String uri, final String localName, final String name) {

        if (ExcelConstants.DIMENSION_TAG.equals(name)) {
            this.curExcelSheet.setRowCount(totalRow);
        }

        // 行
        if (ExcelConstants.ROW_TAG.equals(name)) {
            this.curExcelSheet.addRow(this.curExcelRow);
        }

        // 单元格
        if (ExcelConstants.CELL_TAG.equals(name)) {
            if (StringUtils.isEmpty(tagValue)) {
                this.curExcelCell.setRawValue("");
                this.curExcelCell.setValue("");
            } else {
                this.curExcelCell.setRawValue(StringUtils.trim(curExcelCell.getType().getValue(tagValue)));
            }
            this.curExcelRow.addCell(this.curExcelCell);
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) {
        this.tagValue += new String(ch, start, length);
    }

    @Override
    public void finish() {

    }

    @Override
    public void beforeParseSheet(int sheetIndex) {
        this.curExcelSheet = new TaraSheet(sheetIndex);
        curExcelSheet.setName(sheetNames.get(sheetIndex));
    }

    @Override
    public void afterParseSheet(int sheetIndex) {
        log.info("Sheet[{}] parse finish", sheetIndex);
        this.workbook.addSheet(this.curExcelSheet);
        this.curExcelSheet = null;
    }
}
