package com.sondertara.excel.boot;

import com.sondertara.common.exception.TaraException;
import com.sondertara.common.lang.Partition;
import com.sondertara.common.model.PageResult;
import com.sondertara.excel.base.TaraExcelConfig;
import com.sondertara.excel.base.TaraExcelWriter;
import com.sondertara.excel.common.constants.Constants;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.resolver.ExcelDefaultWriterResolver;
import com.sondertara.excel.task.AbstractExcelGenerateTask;
import com.sondertara.excel.utils.ExcelResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
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
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple Excel writer
 *
 * @author huangxiaohu
 */
@Slf4j
public class ExcelSimpleWriter implements TaraExcelWriter {
    /**
     * The Excel titles
     */
    private List<String> titles;
    /**
     * Current Sheet name
     */
    private String sheetName = "Sheet";
    /**
     * the workbook
     */
    private final SXSSFWorkbook workbook;
    /**
     * sheet index of the workbook
     */
    private AtomicInteger sheetIndex = new AtomicInteger(0);

    /**
     * the pagination data  index has been parsed
     */
    private final AtomicInteger indexParse = new AtomicInteger(0);
    /**
     * handle workbook lock
     */
    private final ReentrantLock lock = new ReentrantLock();
    /**
     * the pagination data to parse
     */
    private final TreeMap<Integer, List<Object[]>> peerDataMap = new TreeMap<>();


    public ExcelSimpleWriter(SXSSFWorkbook workbook) {
        this.sheetName = "sheetName";
        this.workbook = workbook;
        this.sheetIndex.set(workbook.getNumberOfSheets());

    }


    public static ExcelSimpleWriter read(SXSSFWorkbook workbook) {
        return new ExcelSimpleWriter(workbook);
    }

    public static ExcelSimpleWriter create() {
        return new ExcelSimpleWriter(new SXSSFWorkbook(Constants.DEFAULT_ROW_ACCESS_WINDOW_SIZE));
    }


    public ExcelSimpleWriter sheet(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }


    /**
     * set the titles of Excel
     *
     * @param titles the title list
     * @return this
     */
    public ExcelSimpleWriter header(List<String> titles) {
        this.titles = titles;
        return this;
    }

    /**
     * add data by pagination query
     * If it takes time to query data or large amount of data,can use query function which is designed by producer-consumer pattern
     *
     * @param query the query function
     * @return this
     * @see ExportFunction
     */
    public synchronized ExcelSimpleWriter addData(ExportFunction<Object[]> query) {
        AbstractExcelGenerateTask<Object[]> generateTask = new AbstractExcelGenerateTask<Object[]>(query) {
            @Override
            public void parse(PageResult<Object[]> pageResult) {
                List<Object[]> result = pageResult.getData();
                int index = pageResult.getPage();
                try {
                    lock.lock();
                    if (indexParse.get() == index) {
                        write(result);
                        indexParse.incrementAndGet();
                    } else {
                        peerDataMap.put(index, result);
                        if (peerDataMap.containsKey(indexParse.get() + 1)) {
                            write(peerDataMap.get(indexParse.incrementAndGet()));
                        }
                    }
                } catch (Exception e) {
                    log.error("Parse Data error", e);
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }
        };
        generateTask.producers(TaraExcelConfig.CONFIG.getCsvProducerThread());
        generateTask.consumers(2);
        generateTask.start();
        return this;
    }

    /**
     * add data list direct
     *
     * @param dataList the data list
     * @return this
     */
    public synchronized ExcelSimpleWriter addData(List<Object[]> dataList) {
        write(dataList);
        return this;
    }

    /**
     * (non-javadoc)
     * @param mapList data list
     */
    private void write(List<Object[]> mapList) {
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
     * @return poi workbook
     */
    public Workbook generate() {
        peerDataMap.forEach((index, data) -> {
            write(data);

        });
        return this.workbook;
    }

    @Override
    public void to(OutputStream out) {
        try (Workbook wb = generate()) {
            wb.write(out);
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
            cell.setCellValue((Date) cellValue);
        } else if (Number.class.isAssignableFrom(cellValue.getClass())) {
            cell.setCellValue(new BigDecimal(cellValue.toString()).toString());
        } else {
            cell.setCellValue(cellValue.toString());
        }

    }


}
