package com.sondertara.excel;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sondertara.common.util.StringUtils;
import com.sondertara.excel.annotation.ExcelImportFiled;
import com.sondertara.excel.annotation.ExcelExportField;
import com.sondertara.excel.common.Constant;
import com.sondertara.excel.entity.ExcelEntity;
import com.sondertara.excel.entity.ExcelHelper;
import com.sondertara.excel.exception.ExcelTaraException;
import com.sondertara.excel.factory.ExcelMappingFactory;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.function.ImportFunction;
import com.sondertara.excel.parser.ExcelReader;
import com.sondertara.excel.parser.ExcelWriter;
import com.sondertara.excel.task.ExcelGenerateTask;
import com.sondertara.excel.task.ExcelRunnable;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Excel kit.  use this kit you need work with the builder, avoid using the constructor
 *
 * @author huangxiaohu
 */
public class ExcelTara {

    private static final Logger logger = LoggerFactory.getLogger(ExcelTara.class);

    /**
     * task pool
     */
    private ThreadPoolExecutor taskPool;


    private Object param;

    @SuppressWarnings("rawtypes")
    private ExportFunction exportFunction;

    @SuppressWarnings("rawtypes")
    private ImportFunction importFunction;


    /**
     * the class to work
     * <p>
     * {@link ExcelExportField}
     * <p>
     * {@link ExcelExportField}
     */
    private Class<?> excelClass;

    /**
     * row cached in memory, default is 200 {@link Constant#DEFAULT_ROW_ACCESS_WINDOW_SIZE}
     */
    private Integer rowAccessWindowSize;
    /**
     * pre sheet rows count,when export large data will generate multi sheet.
     */
    private Integer recordCountPerSheet;

    private ExcelHelper excelHelper;


    private ExcelTara() {
    }


    protected ExcelTara(Class<?> clazz) {
        this(clazz, null, null, null);
    }

    /**
     * the base constructor
     */
    protected ExcelTara(Class<?> excelClass, Integer rowAccessWindowSize, Integer recordCountPerSheet, Boolean openAutoColumWidth) {

        this.excelClass = excelClass;

        this.rowAccessWindowSize = rowAccessWindowSize;
        this.recordCountPerSheet = recordCountPerSheet;

        ThreadFactory build = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Excel-worker-%d").build();
        this.taskPool = new ThreadPoolExecutor(8, 16, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10), build, new ThreadPoolExecutor.CallerRunsPolicy());
    }


    public static ExcelTara of(Class<?> excelClass) {
        return new ExcelTara(excelClass);
    }


    public ExcelTara pagination(Integer pageStart, Integer pageEnd, Integer pageSize) {
        this.excelHelper = ExcelHelper.builder().pageSize(pageSize).pageStart(pageStart).pageEnd(pageEnd).build();
        return this;
    }

    public <R, EF> ExcelTara handler(R param, ExportFunction<R, EF> exportFunction) {
        this.param = param;
        this.exportFunction = exportFunction;
        return this;
    }

    public <R> ExcelTara handler(ImportFunction<R> importFunction) {
        this.importFunction = importFunction;
        return this;
    }


    /**
     * export to http response with browser
     */
    public void export(String fileName, HttpServletResponse response) {
        try {
            try (SXSSFWorkbook sxssfWorkbook = commonSingleSheet()) {
                download(sxssfWorkbook, response, URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
            } finally {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            }
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }

    /**
     * export to Workbook
     */
    public SXSSFWorkbook exportWorkbook() {
        try {
            return commonSingleSheet();
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }


    /**
     * export to OutputStream to generate large Excel file or upload ftp server
     */
    public void export(OutputStream outputStream) {
        try {
            generateStream(outputStream);
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }

    /**
     * async multi thread export to csv file
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public String exportCsv(String fileName) {
        logger.info("CSV exporting is starting...");
        try {
            verifyAndBuildParams();
            ExcelEntity excelMapping = ExcelMappingFactory.loadExportExcelClass(excelClass);
            final String workPath = Constant.FILE_PATH + File.separator + fileName + File.separator;
            excelHelper.setWorkspace(workPath);
            CyclicBarrier cyclicBarrier = new CyclicBarrier(Constant.PRODUCER_COUNT + Constant.CONSUMER_COUNT + 1);
            ExcelRunnable excelRunnable = new ExcelGenerateTask<>(param, exportFunction, excelMapping, excelHelper);
            for (int i = 0; i < Constant.PRODUCER_COUNT; i++) {
                taskPool.submit((excelRunnable.newRunnableProducer(cyclicBarrier)));
            }
            for (int i = 0; i < Constant.CONSUMER_COUNT; i++) {
                taskPool.submit(excelRunnable.newRunnableConsumer(cyclicBarrier));
            }
            cyclicBarrier.await();
            logger.info("CSV exporting is merging...");
            //合并文件
            ExcelWriter excelWriter = new ExcelWriter(excelMapping, workPath);
            excelWriter.generateCsv(fileName);
            logger.info("CSV exporting has been completed...");
            //返回文件path
            return workPath + fileName + ".csv";
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }

    }

    /**
     * generate export stream.
     */
    private void generateStream(OutputStream outputStream) throws IOException {
        SXSSFWorkbook sxssfWorkbook = null;
        if (outputStream == null) {
            throw new ExcelTaraException("outputStream is null");
        }
        try {
            sxssfWorkbook = commonSingleSheet();
            sxssfWorkbook.write(outputStream);
        } catch (Exception e) {
            logger.error("generate excel stream error!", e);
            if (sxssfWorkbook != null) {
                sxssfWorkbook.close();
            }
            throw new ExcelTaraException(e);
        }
    }

    /**
     * export  multi sheets to http response
     */
    public void exportMultiSheet(String fileName, HttpServletResponse httpServletResponse) {
        try {
            try (SXSSFWorkbook sxssfWorkbook = commonMultiSheet()) {
                download(sxssfWorkbook, httpServletResponse, URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
            }
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }

    /**
     * export  multi sheets to http output stream
     */
    public void exportMultiSheet(OutputStream outputStream) {
        try {
            generateMultiSheetStream(outputStream);
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }

    /**
     * generate multi sheets to output stream
     */
    private void generateMultiSheetStream(OutputStream outputStream) throws IOException {
        SXSSFWorkbook sxssfWorkbook = null;
        try {
            sxssfWorkbook = commonMultiSheet();
            sxssfWorkbook.write(outputStream);
        } catch (Exception e) {
            logger.error("generate multi sheets excel error!", e);
            if (sxssfWorkbook != null) {
                sxssfWorkbook.close();
            }
            throw new ExcelTaraException(e);
        }
    }

    /**
     * export  excel template for import
     */
    public void exportTemplate(String fileName, HttpServletResponse response) {
        SXSSFWorkbook sxssfWorkbook = null;
        try {
            try {
                verifyAndBuildParams();
                ExcelEntity excelMapping = ExcelMappingFactory.loadExportExcelClass(excelClass);
                ExcelWriter excelWriter = new ExcelWriter(excelMapping, excelHelper.getPageSize(), rowAccessWindowSize, recordCountPerSheet);
                sxssfWorkbook = excelWriter.generateTemplateWorkbook();
                download(sxssfWorkbook, response, URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
            } finally {
                if (sxssfWorkbook != null) {
                    sxssfWorkbook.close();
                }
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            }
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }

    /**
     * import all Excel sheet
     */
    public void readExcel(Boolean enableIndex, InputStream inputStream) {
        try {
            if (importFunction == null) {
                throw new ExcelTaraException("excel read handler importFunction is null!");
            }
            if (inputStream == null) {
                throw new ExcelTaraException("inputStream is null");
            }

            ExcelEntity excelMapping = ExcelMappingFactory.loadImportExcelClass(excelClass);
            ExcelReader excelReader = new ExcelReader(excelClass, excelMapping, importFunction, enableIndex);
            excelReader.process(inputStream);
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }

    }

    /**
     * generate one sheet.
     *
     * @param <R> query class
     * @param <F> export class via {@link ExcelImportFiled}
     * @return workbook
     * @throws Exception e
     */
    @SuppressWarnings({"unchecked"})
    private <R, F> SXSSFWorkbook commonSingleSheet() throws Exception {
        verifyAndBuildParams();
        ExcelEntity excelMapping = ExcelMappingFactory.loadExportExcelClass(excelClass);
        ExcelWriter excelWriter = new ExcelWriter(excelMapping, excelHelper.getPageSize(), rowAccessWindowSize, recordCountPerSheet);
        return excelWriter.generateWorkbook(param, exportFunction);
    }

    @SuppressWarnings({"unchecked"})
    private <R, F> SXSSFWorkbook commonMultiSheet() throws Exception {
        verifyAndBuildParams();
        ExcelEntity excelMapping = ExcelMappingFactory.loadExportExcelClass(excelClass);
        ExcelWriter excelWriter = new ExcelWriter(excelMapping, excelHelper.getPageSize(), rowAccessWindowSize, recordCountPerSheet);
        return excelWriter.generateMultiSheetWorkbook(param, exportFunction);
    }

    /**
     * close stream
     */
    private void close(OutputStream out) throws IOException {
        if (null != out) {
            out.flush();
        }
    }

    /**
     * flush excel workbook to file.
     */
    private void download(SXSSFWorkbook wb, HttpServletResponse response, String filename) throws IOException {
        OutputStream out = response.getOutputStream();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition", String.format("attachment; filename=%s", filename));
        if (null != out) {
            wb.write(out);
            out.flush();
        }
    }


    /**
     * validate param and set default value when some field is null.
     */
    private void verifyAndBuildParams() {
        if (excelClass == null) {
            throw new ExcelTaraException("param excelClass is null");
        }
        if (excelHelper == null) {
            throw new ExcelTaraException("param excelHelper is null");
        }


        if (StringUtils.isEmpty(excelHelper.getWorkspace())) {
            excelHelper.setWorkspace("default_export");
        }
        if (excelHelper.getPageSize() == null) {
            excelHelper.setPageSize(Constant.DEFAULT_PAGE_SIZE);
        }
        if (excelHelper.getPageStart() == null) {
            excelHelper.setPageStart(1);
        }
        if (excelHelper.getPageEnd() == null) {
            excelHelper.setPageEnd(Integer.MAX_VALUE);
        }
        if (this.rowAccessWindowSize == null) {
            this.rowAccessWindowSize = Constant.DEFAULT_ROW_ACCESS_WINDOW_SIZE;
        }
    }
}