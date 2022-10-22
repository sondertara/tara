package com.sondertara.excel.analysis;

import com.sondertara.excel.common.constants.ExcelConstants;
import com.sondertara.excel.meta.celltype.ExcelBooleanCellType;
import com.sondertara.excel.meta.celltype.ExcelCellType;
import com.sondertara.excel.meta.celltype.ExcelDateCellType;
import com.sondertara.excel.meta.celltype.ExcelErrorCellType;
import com.sondertara.excel.meta.celltype.ExcelInlineStrCellType;
import com.sondertara.excel.meta.celltype.ExcelNullCellType;
import com.sondertara.excel.meta.celltype.ExcelNumberCellType;
import com.sondertara.excel.meta.celltype.ExcelStringCellType;
import com.sondertara.excel.meta.model.ExcelCellDef;
import com.sondertara.excel.meta.model.ExcelRowDef;
import com.sondertara.excel.processor.ExcelPerRowProcessor;
import com.sondertara.excel.support.callback.RowReadExCallback;
import com.sondertara.excel.utils.ExcelXmlCodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangxiaohu
 */
public class XlsxAnalysisHandler extends DefaultHandler {
    private final StylesTable stylesTable;
    private final SharedStrings sst;
    private final ExcelPerRowProcessor perRowProcessor;
    private int totalRow = 0;
    private String tagValue;

    private final List<String> sheetNames = new ArrayList<>();
    private ExcelRowDef curExcelRow;
    private ExcelCellDef curExcelCell;
    private final List<ExcelCellType> excelCellTypes = new ArrayList<>();
    private final RowReadExCallback rowReadExceptionCallback;

    public XlsxAnalysisHandler(final StylesTable stylesTable, final SharedStrings sst,
                               final ExcelPerRowProcessor perRowProcessor, final RowReadExCallback rowReadExceptionCallback) {
        this.sst = sst;
        this.stylesTable = stylesTable;
        this.perRowProcessor = perRowProcessor;
        this.rowReadExceptionCallback = rowReadExceptionCallback;

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

        if (ExcelConstants.DIMENSION_TAG.equals(localName)) {
            String sheetName = attributes.getValue("name");
            sheetNames.add(sheetName);
        }
        // 总行数
        if (ExcelConstants.DIMENSION_TAG.equals(name)) {
            final String refAttr = attributes.getValue(ExcelConstants.DIMENSION_REF_ATTR);
            this.totalRow = ExcelXmlCodecUtils.getTotalRow(refAttr);
            return;
        }

        // 行
        if (ExcelConstants.ROW_TAG.equals(name)) {
            this.curExcelRow = new ExcelRowDef();
            this.curExcelRow.setRowIndex(Integer.valueOf(attributes.getValue(ExcelConstants.ROW_INDEX_ATTR)));
        }

        // 单元格
        if (ExcelConstants.CELL_TAG.equals(name)) {
            this.curExcelCell = new ExcelCellDef();
            final String abcColIndex = attributes.getValue(ExcelConstants.CELL_ABC_INDEX_ATTR);
            this.curExcelCell.setColIndex(ExcelXmlCodecUtils.getColIndex(abcColIndex));
            this.curExcelCell.setRowIndex(this.curExcelRow.getRowIndex());
            this.curExcelCell.setAbcColIndex(abcColIndex);

            // 设置单元格处理器
            for (final ExcelCellType excelCellType : this.excelCellTypes) {
                if (excelCellType.matches(name, attributes)) {
                    this.curExcelCell.setCellType(excelCellType);
                    break;
                }
            }
        }
        this.tagValue = "";

    }

    @Override
    public void endElement(final String uri, final String localName, final String name) {

        if (ExcelConstants.DIMENSION_TAG.equals(name)) {
            this.perRowProcessor.processTotalRow(this.totalRow);
        }

        // 行
        if (ExcelConstants.ROW_TAG.equals(name)) {
            try {
                this.perRowProcessor.processPerRow(this.curExcelRow);
            } catch (final Exception ex) {
                rowReadExceptionCallback.call(this.curExcelRow, ex);
            }

        }

        // 单元格
        if (ExcelConstants.CELL_TAG.equals(name)) {
            if (StringUtils.isEmpty(tagValue)) {
                this.curExcelCell.setCellValue("");
            } else {
                this.curExcelCell.setCellValue(StringUtils.trim(curExcelCell.getCellType().getValue(tagValue)));
            }
            this.curExcelRow.addExcelCell(this.curExcelCell);
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) {
        this.tagValue += new String(ch, start, length);
    }

}
