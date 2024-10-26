package com.sondertara.excel.boot;


import com.sondertara.excel.base.TaraExcelConfig;
import com.sondertara.excel.base.TaraExcelWriter;
import com.sondertara.excel.common.constants.Constants;
import com.sondertara.excel.common.constants.ExcelExportConstants;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.task.AbstractExcelGenerateTask;
import com.sondertara.excel.task.PageResultWrapper;
import com.sondertara.excel.utils.ExcelResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.dhatim.fastexcel.Workbook;
import org.jspecify.annotations.NonNull;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple Excel writer
 *
 * @author huangxiaohu
 */
@Slf4j
public abstract class ExcelSimpleWriter<T> implements TaraExcelWriter {
    /**
     * The Excel titles
     */
    List<String> titles;
    /**
     * Current Sheet name
     */
    String sheetName = "Sheet";

    int mexSheetCount = TaraExcelConfig.CONFIG.getDefaultRowPerSheet();

    int maxColWidth = ExcelExportConstants.MAX_COL_WIDTH;
    /**
     * the workbook
     */
    T workbook;
    /**
     * sheet index of the workbook
     */
    AtomicInteger sheetIndex = new AtomicInteger(0);

    AtomicBoolean isSheetInitialized = new AtomicBoolean(false);

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
    final TreeMap<Integer, List<Object[]>> peerDataMap = new TreeMap<>();


    ExcelSimpleWriter() {
        this.sheetName = "sheetName";
    }

    public ExcelSimpleWriter(T workbook) {
        this.sheetName = "sheetName";
        this.workbook = workbook;
    }


    public static ExcelSimpleFastWriter read(Workbook workbook) {
        return new ExcelSimpleFastWriter(workbook);
    }

    public static ExcelSimpleWriter<?> create() {
        if (TaraExcelConfig.CONFIG.isUseLegacy()) {
            return new ExcelSimpleLegacyWriter(new SXSSFWorkbook(Constants.DEFAULT_ROW_ACCESS_WINDOW_SIZE));

        }

        return new ExcelSimpleFastWriter();
    }

    public static ExcelSimpleLegacyWriter read(SXSSFWorkbook workbook) {
        return new ExcelSimpleLegacyWriter(workbook);
    }


    public ExcelSimpleWriter<?> sheet(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    /**
     * set the titles of Excel
     *
     * @param titles the title list
     * @return this
     */
    public ExcelSimpleWriter<?> header(List<String> titles) {
        this.titles = titles;
        return this;
    }

    public ExcelSimpleWriter<?> maxSheetCount(int count) {
        if (count > Constants.MAX_RECORD_PER_SHEET) {
            throw new IllegalArgumentException("Sheet row count must less than " + Constants.MAX_RECORD_PER_SHEET);
        }
        this.mexSheetCount = count;
        return this;
    }

    public ExcelSimpleWriter<?> maxColWidth(int width) {
        this.maxColWidth = width;
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


    public synchronized ExcelSimpleWriter<?> addData(ExportFunction<Object[]> query) {
        AbstractExcelGenerateTask<Object[]> generateTask = new AbstractExcelGenerateTask<Object[]>(query) {

            @Override
            protected void consumeData(@NonNull PageResultWrapper<Object[]> data) {
                List<Object[]> result = data.getRaw().getData();
                int index = data.currentIndex();
                if (log.isDebugEnabled()) {
                    log.debug("parse data of index:" + index);
                }

                write(result);
            }
        };
        generateTask.start();
        return this;
    }

    /**
     * add data list direct
     *
     * @param dataList the data list
     * @return this
     */
    public synchronized ExcelSimpleWriter<?> addData(List<Object[]> dataList) {
        write(dataList);
        return this;
    }

    /**
     * (non-javadoc)
     *
     * @param mapList data list
     */
    abstract void write(List<Object[]> mapList);

    /**
     * generate the Excel workbook
     *
     * @return poi workbook
     */
    abstract T generate();


    @Override
    public void to(HttpServletResponse httpServletResponse, String fileName) {
        ExcelResponseUtils.writeResponse(httpServletResponse, fileName, this::to);
    }


}
