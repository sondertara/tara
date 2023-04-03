package com.sondertara.excel.boot;

import com.sondertara.common.exception.TaraException;
import com.sondertara.common.io.FileUtils;
import com.sondertara.common.io.IoUtils;
import com.sondertara.common.lang.Partition;
import com.sondertara.common.lang.id.NanoId;
import com.sondertara.excel.base.TaraExcelConfig;
import com.sondertara.excel.fast.writer.BorderStyle;
import com.sondertara.excel.fast.writer.Color;
import com.sondertara.excel.fast.writer.FastWorkbook;
import com.sondertara.excel.fast.writer.Worksheet;
import com.sondertara.excel.resolver.ExcelDefaultWriterResolver;
import com.sondertara.excel.utils.ExcelResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFCell;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Simple Excel writer
 *
 * @author huangxiaohu
 */
@Slf4j
public class ExcelSimpleFastWriter extends ExcelSimpleWriter<FastWorkbook> {


    private Path tmpFile;


    public ExcelSimpleFastWriter(FastWorkbook workbook) {
        super(workbook);
        this.sheetIndex.set(workbook.getNumberOfSheets());

    }

    public ExcelSimpleFastWriter() {
        try {
            Path path = Paths.get(FileUtils.getTmpDirPath(), "tara", NanoId.randomNanoId());
            FileUtils.mkdir(path.getParent());
            this.tmpFile = path;
            this.workbook = new FastWorkbook(Files.newOutputStream(path), "TaraApplication", "1.0");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.sheetIndex.set(workbook.getNumberOfSheets());

    }


    public static ExcelSimpleFastWriter read(FastWorkbook workbook) {
        return new ExcelSimpleFastWriter(workbook);
    }

    public static ExcelSimpleFastWriter create() {

        return new ExcelSimpleFastWriter();
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
        Worksheet existSheet = getSheet();
        int lastRowNum = existSheet.getLastRowNum();
        LinkedList<Object[]> exitData = new LinkedList<>(mapList);
        for (int i = 0; i < Math.min(mapList.size(), TaraExcelConfig.CONFIG.getDefaultRowPeerSheet() - lastRowNum); i++) {
            createCell(existSheet, lastRowNum + i, exitData.removeFirst(), resolver);
        }
        if (exitData.isEmpty()) {
            return;
        }
        Partition<Object[]> partition = new Partition<>(exitData, TaraExcelConfig.CONFIG.getDefaultRowPeerSheet());
        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        for (List<Object[]> objects : partition) {
            Worksheet newSheet = createSheet(sheetIndex.incrementAndGet());
            CompletableFuture<Void> cf1 = CompletableFuture.runAsync(() -> {
                for (int k = 0; k < objects.size(); k++) {
                    createCell(newSheet, k + 1, objects.get(k), resolver);
                    //resolver.sizeColumnWidth(newSheet, titles.size());
                }
            });
            tasks.add(cf1);
        }
        try {
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
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
    public FastWorkbook generate() {
        if (isSheetInitialized.compareAndSet(false, true)) {
            getSheet();
        }
        peerDataMap.forEach((index, data) -> {
            write(data);

        });
        return this.workbook;
    }

    @Override
    public void to(OutputStream out) {
        try {
            FastWorkbook wb = generate();
            wb.finish();
            IoUtils.copy(new BufferedInputStream(Files.newInputStream(tmpFile)), out);
        } catch (Exception e) {
            throw new TaraException("Write workbook to stream error", e);
        } finally {
            FileUtils.del(tmpFile);
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
    private Worksheet getSheet() {

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
    private Worksheet createSheet(int index) {
        ExcelDefaultWriterResolver resolver = new ExcelDefaultWriterResolver();
        Worksheet sheet = workbook.newWorksheet(sheetName + "_" + (index + 1));
        createHeader(sheet, resolver);
        //resolver.sizeColumnWidth(sheet, titles.size());
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
    private void createCell(Worksheet sheet, int rowNum, Object[] objects, ExcelDefaultWriterResolver resolver) {


        for (int j = 0; j < objects.length; j++) {
            Object value = objects[j];
            sheet.value(rowNum, j, value);
        }
        //for (int j = 0; j < objects.length; j++) {
        //    Object value = objects[j];
        //    SXSSFCell cell = sxssfRow.createCell(j);
        //    buildCellValue(cell, value);
        //    resolver.calculateColumnWidth(cell, j);
        //}

    }

    /**
     * create sheet title
     *
     * @param resolver Excel resolver
     */
    private void createHeader(Worksheet ws, ExcelDefaultWriterResolver resolver) {
        for (int j = 0; j < titles.size(); j++) {
            ws.value(0, j, titles.get(j));
            ws.style(0, j).bold().borderStyle(BorderStyle.THIN).fillColor(Color.EXCEL_GREEN_TITLE).horizontalAlignment("center").fontSize(12).set();
        }

        //CellStyle headCellStyle = null;
        //if (TaraExcelConfig.CONFIG.isOpenAutoColWidth()) {
        //    headerRow.setHeight((short) 400);
        //    headCellStyle = resolver.getHeaderCellStyle(workbook);
        //}
        //for (int j = 0; j < titles.size(); j++) {
        //    SXSSFCell cell = headerRow.createCell(j);
        //    if (Constants.OPEN_CELL_STYLE && null != headCellStyle) {
        //        cell.setCellStyle(headCellStyle);
        //    }
        //    cell.setCellValue(titles.get(j));
        //    resolver.calculateColumnWidth(cell, j);
        //}
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
            cell.setCellValue((Date) cellValue);
        } else if (Number.class.isAssignableFrom(cellValue.getClass())) {
            cell.setCellValue(new BigDecimal(cellValue.toString()).toString());
        } else {
            cell.setCellValue(cellValue.toString());
        }

    }


}
