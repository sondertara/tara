package com.sondertara.excel.parser;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sondertara.excel.constants.Constants;
import com.sondertara.excel.entity.ExcelCellEntity;
import com.sondertara.excel.entity.ExcelWriteSheetEntity;
import com.sondertara.excel.exception.ExcelTaraException;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.task.CsvGenerateTask;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author huangxiaohu
 */
public class ExcelCsvWriterResolver {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);

    private final ExcelWriteSheetEntity excelEntity;



    private final String fileName;


    public ExcelCsvWriterResolver(ExcelWriteSheetEntity excelEntity, String fileName) {
        this.excelEntity = excelEntity;
        this.fileName = fileName;
    }

    public String createFile(ExportFunction<?> exportFunction) {
        try {
            CsvGenerateTask<?> csvGenerateTask = new CsvGenerateTask<>(exportFunction, excelEntity, fileName);
            csvGenerateTask.start();
            logger.info("CSV exporting is merging...");
            generateCsv();
            logger.info("CSV exporting has been completed...");
            // 返回文件path
            final String workPath = Constants.FILE_PATH + File.separator;
            return workPath + fileName + ".csv";
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }

    public void generateCsv() throws IOException {

        final String workPath = Constants.FILE_PATH + File.separator + fileName + File.separator;
        File path = new File(workPath);

        List<File> fileList = new ArrayList<File>();
        if (path.exists()) {
            File[] files = path.listFiles();
            assert files != null;
            if (files.length <= 0) {
                return;
            }
            Collections.addAll(fileList, files);
            final List<File> collect = fileList.stream().sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());
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
            Appendable printWriter = new PrintWriter(csvFile, Constants.CHARSET);
            CSVPrinter csvPrinter = CSVFormat.EXCEL.builder().setHeader(excelEntity.getPropertyList().stream().map(ExcelCellEntity::getColumnName).toArray(String[]::new)).build().print(printWriter);

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
