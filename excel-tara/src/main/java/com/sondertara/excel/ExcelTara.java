package com.sondertara.excel;


import com.sondertara.common.util.StringUtils;
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
import java.util.concurrent.LinkedBlockingQueue;
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
    /**
     * http response
     */
    private HttpServletResponse httpServletResponse;
    /**
     * output stream the excel  file to flush
     */
    private OutputStream outputStream;
    /**
     * input stream the excel file to read
     */
    private InputStream inputStream;
    /**
     * the class to work
     * <p>
     * {@link com.sondertara.excel.annotation.ImportField}
     * <p>
     * {@link com.sondertara.excel.annotation.ImportField}
     */
    private Class excelClass;
    /**
     * excel export helper
     */
    private ExcelHelper helper;
    /**
     * row cached in memory, default is 200 {@link Constant#DEFAULT_ROW_ACCESS_WINDOW_SIZE}
     */
    private Integer rowAccessWindowSize;
    /**
     * pre sheet rows count,when export large data will generate multi sheet.
     */
    private Integer recordCountPerSheet;
    /**
     * enable open cell auto column width ,to keep high performance has removed.
     */
    private Boolean openAutoColumWidth;

    public ExcelTara() {
    }

    /**
     * the constructor for import
     */
    protected ExcelTara(InputStream inputStream, Class excelClass) {
        this(null, null, inputStream, null, excelClass, null, null, null);
    }

    /**
     * OutputStream  export constructor ,can use to export  to ftp server
     */
    protected ExcelTara(OutputStream outputStream, ExcelHelper helper, Class excelClass) {
        this(null, outputStream, null, helper, excelClass,
                Constant.DEFAULT_ROW_ACCESS_WINDOW_SIZE, Constant.DEFAULT_RECORD_COUNT_PEER_SHEET,
                Constant.OPEN_AUTO_COLUMN_WIDTH);
    }

    /**
     * HttpServletResponse export constructor,can use to browser
     */
    protected ExcelTara(HttpServletResponse response, ExcelHelper helper, Class excelClass) {
        this(response, null, null, helper, excelClass,
                Constant.DEFAULT_ROW_ACCESS_WINDOW_SIZE, Constant.DEFAULT_RECORD_COUNT_PEER_SHEET,
                Constant.OPEN_AUTO_COLUMN_WIDTH);
    }

    /**
     * constructor for export
     *
     * @param helper
     * @param clazz
     */
    protected ExcelTara(ExcelHelper helper, Class clazz) {
        this(null, null, null, helper, clazz, null, null, null);
    }

    /**
     * the base constructor
     */
    protected ExcelTara(HttpServletResponse response, OutputStream outputStream,
                        InputStream inputStream
            , ExcelHelper helper, Class excelClass, Integer rowAccessWindowSize,
                        Integer recordCountPerSheet, Boolean openAutoColumWidth) {
        this.httpServletResponse = response;
        this.outputStream = outputStream;
        this.inputStream = inputStream;

        this.excelClass = excelClass;

        this.helper = helper;
        this.rowAccessWindowSize = rowAccessWindowSize;
        this.recordCountPerSheet = recordCountPerSheet;
        this.openAutoColumWidth = openAutoColumWidth;

        this.taskPool = new ThreadPoolExecutor(8, 16, 60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * export to HttpServletResponse,when export excel to browser.
     */
    public static ExcelTara builder(HttpServletResponse httpServletResponse, ExcelHelper helper,
                                    Class clazz) {
        return new ExcelTara(httpServletResponse, helper, clazz);
    }

    /**
     * export to OutputStream, when export large excel for long time or to ftp server.
     */
    public static ExcelTara builder(OutputStream outputStream, ExcelHelper helper, Class clazz) {
        return new ExcelTara(outputStream, helper, clazz);
    }

    /**
     * export to csv file  async with multi thread,when export large data.
     */
    public static ExcelTara builder(ExcelHelper helper, Class clazz) {
        return new ExcelTara(helper, clazz);
    }

    /**
     * export to HttpServletResponse with row cache config .
     */
    public static ExcelTara builder(HttpServletResponse response, ExcelHelper helper,
                                    Class excelClass, Integer rowAccessWindowSize, Integer recordCountPerSheet,
                                    Boolean openAutoColumWidth) {
        return new ExcelTara(response, null, null
                , helper, excelClass, rowAccessWindowSize, recordCountPerSheet,
                openAutoColumWidth);
    }

    /**
     * export to OutputStream with cache config.
     */
    public static ExcelTara builder(OutputStream outputStream, ExcelHelper helper,
                                    Class excelClass, Integer rowAccessWindowSize, Integer recordCountPerSheet, Boolean openAutoColumWidth) {
        return new ExcelTara(null, outputStream, null
                , helper, excelClass, rowAccessWindowSize, recordCountPerSheet,
                openAutoColumWidth);
    }

    /**
     * import excel builder.
     */
    public static ExcelTara builder(InputStream inputStream, Class clazz) {
        return new ExcelTara(inputStream, clazz);
    }

    /**
     * export to http response with browser
     */
    public <R, T> void exportResponse(R param, ExportFunction<R, T> exportFunction) {
        SXSSFWorkbook sxssfWorkbook = null;
        try {
            try {
                verifyResponse();
                sxssfWorkbook = commonSingleSheet(param, exportFunction);
                download(sxssfWorkbook, httpServletResponse,
                        URLEncoder.encode(helper.getFileName() + ".xlsx", "UTF-8"));
            } finally {
                if (sxssfWorkbook != null) {
                    sxssfWorkbook.close();
                }
                if (httpServletResponse != null && httpServletResponse.getOutputStream() != null) {
                    httpServletResponse.getOutputStream().close();
                }
            }
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }

    /**
     * export to OutputStream to generate large excel file or upload ftp server
     */
    public <R, T> void exportStream(R param, ExportFunction<R, T> exportFunction) {
        OutputStream outputStream = null;
        try {
            try {
                outputStream = generateStream(param, exportFunction);
                close(outputStream);
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }

    /**
     * async multi thread export to csv file
     */
    public <R, T> String exportCsv(R param, ExportFunction<R, T> exportFunction) {
        logger.info("开始导出csv");
        try {
            verifyAndBuildParams();
            ExcelEntity excelMapping = ExcelMappingFactory.loadExportExcelClass(excelClass, helper.getFileName());
            final String workPath = Constant.FILE_PATH + helper.getUser() + File.separator + helper.getFileName() + File.separator;
            helper.setWorkspace(workPath);
            ExcelRunnable excelRunnable = new ExcelGenerateTask<R, T>(param, exportFunction, excelMapping, helper);
            for (int i = 0; i < Constant.PRODUCER_COUNT; i++) {

                taskPool.submit((excelRunnable.newRunnableProducer()));
            }

            for (int i = 0; i < Constant.CONSUMER_COUNT; i++) {
                taskPool.submit(excelRunnable.newRunnableConsumer());
            }

            taskPool.shutdown();
            while (true) {
                if (taskPool.isTerminated()) {
                    logger.info("文件处理结束");
                    //合并文件
                    ExcelWriter excelWriter = new ExcelWriter(excelMapping, workPath);
                    excelWriter.generateCsv();
                    logger.info("csv生成完毕");
                    break;
                }
            }
            //返回文件path
            return workPath + helper.getFileName() + ".csv";
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }

    }

    /**
     * generate export stream.
     */
    private <R, T> OutputStream generateStream(R param, ExportFunction<R, T> exportFunction)
            throws IOException {
        SXSSFWorkbook sxssfWorkbook = null;
        try {
            verifyStream();
            sxssfWorkbook = commonSingleSheet(param, exportFunction);
            sxssfWorkbook.write(outputStream);
            return outputStream;
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
    public <R, T> void exportMultiSheetResponse(R param, ExportFunction<R, T> exportFunction) {
        SXSSFWorkbook sxssfWorkbook = null;
        try {
            try {
                verifyResponse();
                sxssfWorkbook = commonMultiSheet(param, exportFunction);
                download(sxssfWorkbook, httpServletResponse,
                        URLEncoder.encode(helper.getFileName() + ".xlsx", "UTF-8"));
            } finally {
                if (sxssfWorkbook != null) {
                    sxssfWorkbook.close();
                }
            }
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }

    /**
     * export  multi sheets to http output stream
     */
    public <R, T> void exportMultiSheetStream(R param, ExportFunction<R, T> exportFunction) {
        OutputStream outputStream = null;
        try {
            try {
                outputStream = generateMultiSheetStream(param, exportFunction);
                close(outputStream);
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }

    /**
     * generate multi sheets to output stream
     */
    private <R, T> OutputStream generateMultiSheetStream(R param,
                                                         ExportFunction<R, T> exportFunction) throws IOException {
        SXSSFWorkbook sxssfWorkbook = null;
        try {
            verifyStream();
            sxssfWorkbook = commonMultiSheet(param, exportFunction);
            sxssfWorkbook.write(outputStream);
            return outputStream;
        } catch (Exception e) {
            logger.error("generate multi sheets excel error!", e);
            if (sxssfWorkbook != null) {
                sxssfWorkbook.close();
            }
            throw new ExcelTaraException(e);
        }
    }

    /**
     * export  excel tmplate for import
     */
    public void exportTemplate() {
        SXSSFWorkbook sxssfWorkbook = null;
        try {
            try {
                verifyResponse();
                verifyAndBuildParams();
                ExcelEntity excelMapping = ExcelMappingFactory
                        .loadExportExcelClass(excelClass, helper.getFileName());
                ExcelWriter excelWriter = new ExcelWriter(excelMapping, helper.getPageSize(),
                        rowAccessWindowSize, recordCountPerSheet);
                sxssfWorkbook = excelWriter.generateTemplateWorkbook();
                download(sxssfWorkbook, httpServletResponse,
                        URLEncoder.encode(helper.getFileName() + ".xlsx", "UTF-8"));
            } finally {
                if (sxssfWorkbook != null) {
                    sxssfWorkbook.close();
                }
                if (httpServletResponse != null && httpServletResponse.getOutputStream() != null) {
                    httpServletResponse.getOutputStream().close();
                }
            }
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }

    /**
     * import all excel sheet
     */
    public void importExcel(Boolean enableIndex, ImportFunction importFunction) {
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
     * genetare one sheet.
     *
     * @param param          query param
     * @param exportFunction export function
     * @param <R>            query class
     * @param <T>            export class via {@link com.sondertara.excel.annotation.ExportField}
     * @return workbook
     * @throws Exception
     */
    private <R, T> SXSSFWorkbook commonSingleSheet(R param, ExportFunction<R, T> exportFunction)
            throws Exception {
        verifyAndBuildParams();
        ExcelEntity excelMapping = ExcelMappingFactory.loadExportExcelClass(excelClass, helper.getFileName());
        ExcelWriter excelWriter = new ExcelWriter(excelMapping, helper.getPageSize(), rowAccessWindowSize,
                recordCountPerSheet);
        return excelWriter.generateWorkbook(param, exportFunction);
    }

    private <R, T> SXSSFWorkbook commonMultiSheet(R param, ExportFunction<R, T> exportFunction)
            throws Exception {
        verifyAndBuildParams();
        ExcelEntity excelMapping = ExcelMappingFactory.loadExportExcelClass(excelClass, helper.getFileName());
        ExcelWriter excelWriter = new ExcelWriter(excelMapping, helper.getPageSize(), rowAccessWindowSize,
                recordCountPerSheet);
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
    private void download(SXSSFWorkbook wb, HttpServletResponse response, String filename)
            throws IOException {
        OutputStream out = response.getOutputStream();
        response
                .setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition",
                String.format("attachment; filename=%s", filename));
        if (null != out) {
            wb.write(out);
            out.flush();
        }
    }

    /**
     * validate http response
     */
    private void verifyResponse() {
        if (httpServletResponse == null) {
            throw new ExcelTaraException("httpServletResponse is null");
        }
    }

    /**
     * validate  output stream
     */
    private void verifyStream() {
        if (outputStream == null) {
            throw new ExcelTaraException("outputStream is null");
        }
    }

    /**
     * validate param and set default value when some field is null.
     */
    private void verifyAndBuildParams() {
        if (excelClass == null) {
            throw new ExcelTaraException("param excelClass is null");
        }
        if (helper == null) {
            throw new ExcelTaraException("param excelHelper is null");
        }

        if (StringUtils.isEmpty(helper.getFileName())) {
            throw new ExcelTaraException("param fileName is null");
        }
        if (StringUtils.isEmpty(helper.getUser())) {
            helper.setUser("default_export");
        }
        if (helper.getPageSize() == null) {
            helper.setPageSize(Constant.DEFAULT_PAGE_SIZE);
        }
        if (helper.getPageStart() == null) {
            helper.setPageStart(1);
        }
        if (helper.getPageEnd() == null) {
            helper.setPageEnd(Integer.MAX_VALUE);
        }
        if (this.rowAccessWindowSize == null) {
            this.rowAccessWindowSize = Constant.DEFAULT_ROW_ACCESS_WINDOW_SIZE;
        }
    }

}