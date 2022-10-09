package com.sondertara.excel.parser;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sondertara.excel.common.Constant;
import com.sondertara.excel.entity.ExcelCellEntity;
import com.sondertara.excel.entity.ExcelWriteSheetEntity;
import com.sondertara.excel.entity.PageQueryParam;
import com.sondertara.excel.exception.ExcelTaraException;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.task.ExcelGenerateTask;
import com.sondertara.excel.task.ExcelRunnable;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author huangxiaohu
 */
public class ExcelCsvWriterResolver {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);

    private static final ThreadPoolExecutor TASK_POOL = new ThreadPoolExecutor(8, 16, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Excel-worker-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    private final ExcelWriteSheetEntity excelEntity;
    private final String fileName;

    private Integer nullCellCount = 0;

    private XSSFCellStyle headCellStyle;
    private final Map<Integer, Integer> columnWidthMap = new HashMap<>();

    public ExcelCsvWriterResolver(ExcelWriteSheetEntity excelEntity, String fileName) {
        this.excelEntity = excelEntity;
        this.fileName = fileName;
    }

    @SuppressWarnings("unchecked")
    public String createFile(PageQueryParam pageQueryParam, ExportFunction<?> exportFunction) {
        try {
            CyclicBarrier cyclicBarrier = new CyclicBarrier(Constant.PRODUCER_COUNT + Constant.CONSUMER_COUNT + 1);
            ExcelRunnable excelRunnable = new ExcelGenerateTask<>(pageQueryParam, exportFunction, excelEntity,
                    fileName);
            for (int i = 0; i < Constant.PRODUCER_COUNT; i++) {
                TASK_POOL.submit((excelRunnable.newRunnableProducer(cyclicBarrier)));
            }
            for (int i = 0; i < Constant.CONSUMER_COUNT; i++) {
                TASK_POOL.submit(excelRunnable.newRunnableConsumer(cyclicBarrier));
            }
            cyclicBarrier.await();
            logger.info("CSV exporting is merging...");
            generateCsv();
            logger.info("CSV exporting has been completed...");
            // 返回文件path
            final String workPath = Constant.FILE_PATH + File.separator + fileName + File.separator;
            return workPath + fileName + ".csv";
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }

    public void generateCsv() throws IOException {

        final String workPath = Constant.FILE_PATH + File.separator + fileName + File.separator;
        File path = new File(workPath);

        List<File> fileList = new ArrayList<File>();
        if (path.exists()) {
            File[] files = path.listFiles();
            assert files != null;
            if (files.length <= 0) {
                return;
            }
            Collections.addAll(fileList, files);
            final List<File> collect = fileList.stream().sorted(Comparator.comparing(File::getName))
                    .collect(Collectors.toList());
            File csvFile = new File(workPath + fileName + ".csv");

            if (csvFile.exists()) {
                boolean delete = csvFile.delete();
                if (!delete) {
                    throw new IOException("Delete file:" + csvFile.getAbsolutePath() + " failed");
                }
            } else {
                boolean newFile = csvFile.createNewFile();
                if (!newFile) {
                    throw new IOException("Create file:" + csvFile.getAbsolutePath() + " failed");
                }
            }
            Appendable printWriter = new PrintWriter(csvFile, Constant.CHARSET);
            CSVPrinter csvPrinter = CSVFormat.EXCEL.builder().setHeader(
                    excelEntity.getPropertyList().stream().map(ExcelCellEntity::getColumnName).toArray(String[]::new))
                    .build().print(printWriter);

            csvPrinter.flush();
            csvPrinter.close();
            for (File file : collect) {
                if (file.getName().endsWith("csv")) {
                    byte[] bytes = FileUtils.readFileToByteArray(file);
                    FileUtils.writeByteArrayToFile(csvFile, bytes, true);
                }
                if (!file.getName().contains(fileName)) {
                    file.delete();
                }
            }
        }

    }
}
