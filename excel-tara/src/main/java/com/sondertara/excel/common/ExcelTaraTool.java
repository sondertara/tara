package com.sondertara.excel.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sondertara.common.util.CollectionUtils;
import com.sondertara.common.util.StringUtils;
import com.sondertara.excel.entity.ErrorEntity;
import com.sondertara.excel.exception.ExcelTaraException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sondertara.excel.common.Constant.FILE_STORAGE_PREFIX;

/**
 * excel tara tools
 * <p>
 * date 2019/12/15 6:32 下午
 *
 * @author huangxiaohu
 * @version 1.0
 * @since 1.0
 **/
public class ExcelTaraTool {

    private static final Logger logger = LoggerFactory.getLogger(ExcelTaraTool.class);
    public static final String WORKSPACE = System.getProperty("user.dir") + File.separator + FILE_STORAGE_PREFIX;

    private static ThreadLocal<List<List<String>>> errorEntityRecordsLocal = new ThreadLocal<>();
    private static ThreadLocal<Map<String, CSVPrinter>> printerThreadLocal = new ThreadLocal<>();

    public static List<List<String>> getErrorEntityRecords() {

        List<List<String>> list = errorEntityRecordsLocal.get();
        removeErrorEntityList();
        return list;
    }

    public static void removeErrorEntityList() {
        errorEntityRecordsLocal.remove();
    }

    public static void addErrorEntity(ErrorEntity errorEntity) {
        List<List<String>> lists = errorEntityRecordsLocal.get();
        if (lists == null) {
            lists = Lists.newArrayList();
        }
        List<String> record = new ArrayList<>(5);
        record.add(errorEntity.getSheetIndex().toString());
        record.add(errorEntity.getRowIndex().toString());
        record.add(errorEntity.getCellIndex().toString());
        record.add(errorEntity.getColumnName());
        record.add(errorEntity.getCellValue());
        record.add(errorEntity.getErrorMessage());
        lists.add(record);
        errorEntityRecordsLocal.set(lists);
    }


    /**
     * get work file
     *
     * @param fileName file name with [.csv] suffix
     * @return file
     */
    public static File getPrinterFile(String fileName) {
        return getWorkFile(fileName);

    }

    /**
     * Prints collections/arrays as records
     *
     * @param fileName file name with [.csv] suffix
     * @param values   records
     */
    public static void writeRecords(String fileName, final Iterable<?> values, List<String> title) {
        try {
            CSVPrinter csvPrinter = getPrinter(fileName, title);
            csvPrinter.printRecords(values);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static CSVPrinter getPrinter(String fileName, List<String> title) throws IOException {

        Map<String, CSVPrinter> printerMap = printerThreadLocal.get();

        CSVPrinter csvPrinter = null;
        if (printerMap == null) {

            File errorFile = new File(generateWorkspaceFilePath(fileName));
            Appendable writer = new PrintWriter(errorFile, Constant.CHARSET);
            if (CollectionUtils.isEmpty(title)) {
                csvPrinter = CSVFormat.EXCEL.print(writer);
            } else {
                csvPrinter = CSVFormat.EXCEL.withHeader(title.toArray(new String[title.size()])).print(writer);
            }
            printerMap = Maps.newHashMap();
            printerMap.put(fileName, csvPrinter);
            printerThreadLocal.set(printerMap);

        } else {
            csvPrinter = printerMap.get(fileName);
            if (null == csvPrinter) {
                File errorFile = new File(generateWorkspaceFilePath(fileName));
                Appendable writer = new PrintWriter(errorFile, Constant.CHARSET);
                if (CollectionUtils.isEmpty(title)) {
                    csvPrinter = CSVFormat.EXCEL.print(writer);
                } else {
                    csvPrinter = CSVFormat.EXCEL.withHeader(title.toArray(new String[title.size()])).print(writer);
                }

                printerMap.put(fileName, csvPrinter);
                printerThreadLocal.set(printerMap);
            }
        }
        return csvPrinter;
    }

    /**
     * Prints collections/arrays as records
     *
     * @param fileName file name with [.csv] suffix
     * @param values   records
     */
    public static void writeRecords(String fileName, final Iterable<?> values) {
        try {
            CSVPrinter csvPrinter = getPrinter(fileName, null);
            csvPrinter.printRecords(values);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void closeAllPrinter() {
        Map<String, CSVPrinter> printerMap = printerThreadLocal.get();
        try {

            if (printerMap != null) {
                for (Map.Entry<String, CSVPrinter> entry : printerMap.entrySet()) {

                    CSVPrinter printer = entry.getValue();
                    if (null != printer) {
                        printer.flush();
                        printer.close();
                    }
                }
            }
            printerThreadLocal.remove();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param fileName file name with [.csv] suffix
     */
    public static void closePrinter(String fileName) {
        Map<String, CSVPrinter> printerMap = printerThreadLocal.get();
        try {
            if (printerMap != null) {
                CSVPrinter printer = printerMap.get(fileName);
                if (null != printer) {
                    printer.flush();
                    printer.close();
                    if (printerMap.size() == 1) {
                        printerThreadLocal.remove();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param fileName file name with  suffix
     * @return the path to work
     * @throws ExcelTaraException errors
     */
    public static String generateWorkspaceFilePath(String fileName) throws ExcelTaraException {

        if (StringUtils.isEmpty(fileName)) {
            throw new ExcelTaraException("fileName is empty");
        }

        File localFile = new File(WORKSPACE);
        if (!localFile.exists()) {
            localFile.mkdirs();
        }

        String path = WORKSPACE + File.separator + fileName;


        localFile = new File(path);
        if (localFile.exists()) {
            boolean delete = localFile.delete();
            if (delete == false) {
                logger.error("Delete exist file \"{}\" failed!!!", path);
                throw new ExcelTaraException("Delete exist file \"{}\" failed!!!", path);
            }
        }
        logger.info("create work file path = {}", path);
        return path;

    }

    /**
     * @param fileName file name
     * @return the path to work
     * @throws ExcelTaraException errors¬
     */
    public static File getWorkFile(String fileName) throws ExcelTaraException {

        if (StringUtils.isEmpty(fileName)) {
            throw new ExcelTaraException("fileName is empty");
        }


        String path = WORKSPACE + File.separator + fileName;

        File localFile = new File(path);
        if (!localFile.exists()) {
            throw new ExcelTaraException("no file exist  \"{}\"", path);

        }
        logger.info("get work file path = {}", path);
        return localFile;

    }
}
