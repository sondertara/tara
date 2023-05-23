package com.sondertara.excel.boot;

import com.sondertara.common.exception.TaraException;
import com.sondertara.common.lang.Partition;
import com.sondertara.common.time.DatePattern;
import com.sondertara.excel.base.TaraExcelConfig;
import com.sondertara.excel.common.constants.Constants;
import com.sondertara.excel.resolver.ExcelDefaultWriterResolver;
import com.sondertara.excel.utils.ExcelResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple Excel writer
 *
 * @author huangxiaohu
 */
@Slf4j
public class ExcelSimpleLegacyWriter extends ExcelSimpleWriter<SXSSFWorkbook> {

    public ExcelSimpleLegacyWriter(SXSSFWorkbook workbook) {
        super(workbook);
        this.sheetIndex.set(workbook.getNumberOfSheets());

    }

    /**
     * (non-javadoc)
     *
     * @param mapList data list
     */
    @Override
    void write(List<Object[]> mapList) {
        isSheetInitialized.compareAndSet(false, true);
        if (log.isDebugEnabled()) {
            log.debug("Write workbook start[{}]", Thread.currentThread().getName());
        }
        ExcelDefaultWriterResolver resolver = new ExcelDefaultWriterResolver();
        SXSSFSheet existSheet = getSheet();
        int lastRowNum = existSheet.getLastRowNum();
        LinkedList<Object[]> exitData = new LinkedList<>(mapList);
        for (int i = 0; i < Math.min(mapList.size(), TaraExcelConfig.CONFIG.getDefaultRowPeerSheet() - lastRowNum); i++) {
            createCell(existSheet, lastRowNum + i + 1, exitData.removeFirst(), resolver);
        }
        if (exitData.isEmpty()) {
            return;
        }
        Partition<Object[]> partition = new Partition<>(exitData, TaraExcelConfig.CONFIG.getDefaultRowPeerSheet());
        for (List<Object[]> objects : partition) {
            SXSSFSheet newSheet = createSheet(sheetIndex.incrementAndGet());
            for (int k = 0; k < objects.size(); k++) {
                createCell(newSheet, k + 1, objects.get(k), resolver);
            }
            resolver.sizeColumnWidth(newSheet, titles.size());
        }
        exitData.clear();
        if (log.isDebugEnabled()) {
            log.debug("Write workbook end[{}]", Thread.currentThread().getName());
        }
    }

    /**
     * generate the Excel workbook
     *
     * @return poi workbook
     */
    @Override
    public SXSSFWorkbook generate() {
        if (isSheetInitialized.compareAndSet(false, true)) {
            getSheet();
        }
        peerDataMap.forEach((index, data) -> {
            write(data);

        });
        return workbook;
    }

    @Override
    public void to(OutputStream out) {
        try (SXSSFWorkbook wb = generate()) {
            wb.write(out);
            wb.dispose();
        } catch (Exception e) {
            throw new TaraException("Write workbook to stream error", e);
        }
    }

    @Override
    public void to(HttpServletResponse httpServletResponse, String fileName) {
        ExcelResponseUtils.writeResponse(httpServletResponse, fileName, this::to);
    }

    /**
     * get sheet by sheetIndex
     * if cause error then create new sheet by the sheetIndex
     *
     * @return current sheet
     */
    private SXSSFSheet getSheet() {

        try {
            return workbook.getSheetAt(sheetIndex.get());
        } catch (Exception e) {
            return createSheet(sheetIndex.get());
        }
    }

    /**
     * create new sheet
     *
     * @param index the index
     * @return sheet
     */
    private SXSSFSheet createSheet(int index) {
        ExcelDefaultWriterResolver resolver = new ExcelDefaultWriterResolver();
        SXSSFSheet sheet = workbook.createSheet(sheetName + "_" + (index + 1));
        SXSSFRow headerRow = sheet.createRow(0);
        createHeader(headerRow, resolver);
        resolver.sizeColumnWidth(sheet, titles.size());
        return sheet;
    }

    /**
     * create cell for one row
     *
     * @param sheet    current sheet
     * @param rowNum   row index
     * @param objects  data of one row
     * @param resolver Excel resolver
     */
    private void createCell(SXSSFSheet sheet, int rowNum, Object[] objects, ExcelDefaultWriterResolver resolver) {
        SXSSFRow sxssfRow = sheet.createRow(rowNum);

        for (int j = 0; j < objects.length; j++) {
            Object value = objects[j];
            SXSSFCell cell = sxssfRow.createCell(j);
            buildCellValue(cell, value);
            resolver.calculateColumnWidth(cell, j);
        }

    }

    /**
     * create sheet title
     *
     * @param headerRow title row
     * @param resolver  Excel resolver
     */
    private void createHeader(SXSSFRow headerRow, ExcelDefaultWriterResolver resolver) {
        CellStyle headCellStyle = null;
        if (TaraExcelConfig.CONFIG.isOpenAutoColWidth()) {
            headerRow.setHeight((short) 400);
            headCellStyle = resolver.getHeaderCellStyle(workbook);
        }
        for (int j = 0; j < titles.size(); j++) {
            SXSSFCell cell = headerRow.createCell(j);
            if (Constants.OPEN_CELL_STYLE && null != headCellStyle) {
                cell.setCellStyle(headCellStyle);
            }
            cell.setCellValue(titles.get(j));
            resolver.calculateColumnWidth(cell, j);
        }
    }

    /**
     * build simple cell data
     *
     * @param cell      the cell
     * @param cellValue the data
     */
    private void buildCellValue(SXSSFCell cell, Object cellValue) {
        if (cellValue == null) {
            cell.setCellValue("");
        } else if (cellValue instanceof BigDecimal) {
            cell.setCellValue(cellValue.toString());

        } else if (cellValue instanceof Date) {
            short format = workbook.createDataFormat().getFormat(DatePattern.NORM_DATETIME_PATTERN);
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(format);
            cell.setCellStyle(cellStyle);
            cell.setCellValue((Date) cellValue);

        } else if (Number.class.isAssignableFrom(cellValue.getClass())) {
            cell.setCellValue(new BigDecimal(cellValue.toString()).toString());
        } else {
            cell.setCellValue(cellValue.toString());
        }

    }


}
