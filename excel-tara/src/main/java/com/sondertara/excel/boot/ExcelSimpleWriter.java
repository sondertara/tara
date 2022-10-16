package com.sondertara.excel.boot;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sondertara.common.exception.TaraException;
import com.sondertara.common.lang.Partition;
import com.sondertara.common.util.CollectionUtils;
import com.sondertara.excel.base.TaraExcelConfig;
import com.sondertara.excel.base.TaraExcelWriter;
import com.sondertara.excel.constants.Constants;
import com.sondertara.excel.entity.PageResult;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.parser.ExcelDefaultWriterResolver;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple Excel writer
 *
 * @author huangxiaohu
 */
@Slf4j
public class ExcelSimpleWriter implements TaraExcelWriter {

    private static final ThreadPoolExecutor TASK_POOL = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, Runtime.getRuntime().availableProcessors() * 2 + 16, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(30), new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Excel-worker-%d").build(), new ThreadPoolExecutor.CallerRunsPolicy());
    private CellStyle headCellStyle;

    private List<String> titles;
    private String sheetName = "Sheet";
    private final SXSSFWorkbook workbook;

    private AtomicInteger sheetIndex = new AtomicInteger(0);


    private AtomicInteger index = new AtomicInteger(1);
    private AtomicInteger indexParse = new AtomicInteger(0);
    private ReentrantLock lock = new ReentrantLock();

    private AtomicBoolean process = new AtomicBoolean(true);

    private final TreeMap<Integer, List<Object[]>> peerDataMap = new TreeMap<>();

    private final Phaser phaser = new Phaser();

    public ExcelSimpleWriter(SXSSFWorkbook workbook) {
        this.sheetName = "sheetName";
        this.workbook = workbook;
        this.sheetIndex.set(workbook.getNumberOfSheets());
        phaser.register();
    }


    public static ExcelSimpleWriter of(SXSSFWorkbook workbook) {
        return new ExcelSimpleWriter(workbook);
    }

    public static ExcelSimpleWriter newWorkbook() {
        return new ExcelSimpleWriter(new SXSSFWorkbook(Constants.DEFAULT_ROW_ACCESS_WINDOW_SIZE));
    }


    public ExcelSimpleWriter sheet(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }


    public ExcelSimpleWriter header(List<String> titles) {
        this.titles = titles;
        return this;
    }

    public synchronized ExcelSimpleWriter addData(ExportFunction<Object[]> query) {
        AbstractExcelGenerateTask<Object[]> generateTask = new AbstractExcelGenerateTask<Object[]>(query) {
            @Override
            public void parse(PageResult<Object[]> pageResult) {
                List<Object[]> result = pageResult.getData();
                int index = pageResult.getPage();
                if (CollectionUtils.isEmpty(result)) {
                    process.set(true);
                    return;
                }
                try {
                    lock.lock();
                    if (indexParse.get() == index) {
                        write(result);
                        indexParse.incrementAndGet();
                    } else if (peerDataMap.containsKey(indexParse.get() + 1)) {
                        write(peerDataMap.get(indexParse.get() + 1));
                    } else {
                        peerDataMap.put(index, result);
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

    public synchronized ExcelSimpleWriter addData(List<Object[]> dataList) {
        write(dataList);
        return this;
    }

    private void write(List<Object[]> mapList) {
        ExcelDefaultWriterResolver resolver = new ExcelDefaultWriterResolver();
        SXSSFSheet sheet = null;
        try {
            sheet = workbook.getSheetAt(sheetIndex.get());
        } catch (Exception e) {
            sheet = workbook.createSheet(sheetName + "_" + sheetIndex);
            SXSSFRow headerRow = sheet.createRow(0);
            createHeader(headerRow, resolver);
        }
        int lastRowNum = sheet.getLastRowNum();
        LinkedList<Object[]> exitData = new LinkedList<>(mapList);

        for (int i = 0; i < Math.min(mapList.size(), Constants.DEFAULT_RECORD_COUNT_PEER_SHEET - lastRowNum); i++) {
            createCell(sheet, lastRowNum + i + 1, exitData.removeFirst(), resolver);
        }
        resolver.sizeColumnWidth(sheet, titles.size());
        if (exitData.isEmpty()) {
            return;
        }
        Partition<Object[]> partition = new Partition<>(exitData, Constants.DEFAULT_RECORD_COUNT_PEER_SHEET);
        for (int i = 0; i < partition.size(); i++) {
            List<Object[]> objects = partition.get(i);
            sheet = workbook.createSheet(sheetName + "_" + sheetIndex.incrementAndGet());
            SXSSFRow headerRow = sheet.createRow(0);
            createHeader(headerRow, resolver);
            for (int k = 0; k < objects.size(); k++) {
                createCell(sheet, k + 1, objects.get(k), resolver);
            }
            resolver.sizeColumnWidth(sheet, titles.size());
        }

        exitData.clear();
    }

    private void createHeader(SXSSFRow headerRow, ExcelDefaultWriterResolver resolver) {
        if (Constants.OPEN_CELL_STYLE) {
            headerRow.setHeight((short) 400);
            headCellStyle = resolver.getHeaderCellStyle(workbook);
        }
        for (int j = 0; j < titles.size(); j++) {
            SXSSFCell cell = headerRow.createCell(j);
            if (Constants.OPEN_CELL_STYLE) {
                cell.setCellStyle(headCellStyle);
            }
            cell.setCellValue(titles.get(j));
            resolver.calculateColumnWidth(cell, j);
        }
    }

    private void createCell(SXSSFSheet sheet, int rowNum, Object[] objects, ExcelDefaultWriterResolver resolver) {
        SXSSFRow sxssfRow = sheet.createRow(rowNum);

        for (int j = 0; j < objects.length; j++) {
            Object value = objects[j];
            SXSSFCell cell = sxssfRow.createCell(j);
            buildCellValue(cell, value);
            resolver.calculateColumnWidth(cell, j);
        }

    }


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


}
