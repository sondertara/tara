package com.sondertara.excel.task;

import com.sondertara.common.util.LocalDateTimeUtils;
import com.sondertara.excel.constants.Constants;
import com.sondertara.excel.entity.ExcelCellEntity;
import com.sondertara.excel.entity.ExcelWriteSheetEntity;
import com.sondertara.excel.entity.PageResult;
import com.sondertara.excel.function.ExportFunction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CsvGenerateTask<R> extends AbstractExcelGenerateTask<R> {
    private static final Logger logger = LoggerFactory.getLogger(CsvGenerateTask.class);
    private final ExcelWriteSheetEntity excelEntity;

    public final String fileName;

    public CsvGenerateTask(ExportFunction<R> exportFunction, ExcelWriteSheetEntity e, String fileName) {
        super(exportFunction);
        this.excelEntity = e;
        this.fileName = fileName;
    }

    @Override
    public void parse(PageResult<R> excelQueryEntity) {
        if (logger.isDebugEnabled()) {
            logger.debug("Data of page[{}] processing  is starting ......", excelQueryEntity.getPage());
        }
        try {
            final String workPath = Constants.FILE_PATH + File.separator + fileName + File.separator;
            File file = new File(workPath);
            if (!file.exists()) {
                boolean mkdir = file.mkdirs();
                if (!mkdir) {
                    throw new IOException("Create directory:" + file.getAbsolutePath() + " error");
                }
            }
            Appendable printWriter = new PrintWriter(workPath + excelQueryEntity.getPage() + ".csv", Constants.CHARSET);
            CSVPrinter csvPrinter = CSVFormat.EXCEL.print(printWriter);

            final List<R> list = excelQueryEntity.getData();
            for (R data : list) {
                List<String> row = buildRow(data, excelEntity);
                csvPrinter.printRecord(row);
            }

            csvPrinter.flush();
            csvPrinter.close();
            if (logger.isDebugEnabled()) {
                logger.debug("Data of page[{}] processing has been completed...", excelQueryEntity.getPage());
            }
        } catch (Exception e) {
            logger.error("write into file error:", e);
        }
    }

    /**
     * build data row except first row in Excel.
     *
     * @param entity      data
     * @param excelEntity excel entity via
     *                    {@link com.sondertara.excel.meta.annotation.ExcelExportField}
     */
    private List<String> buildRow(Object entity, ExcelWriteSheetEntity excelEntity) throws IllegalAccessException {

        List<ExcelCellEntity> propertyList = excelEntity.getPropertyList();
        List<String> list = new ArrayList<>(propertyList.size());
        for (ExcelCellEntity property : propertyList) {
            String cell;
            Field field = property.getFieldEntity();
            Object cellValue = field.get(entity);

            if (cellValue == null) {
                cell = "";
            } else if (cellValue instanceof BigDecimal) {
                cell = (((BigDecimal) cellValue).setScale(property.getScale(), property.getRoundingMode())).toString();
            } else if (cellValue instanceof Date) {
                cell = LocalDateTimeUtils.format((Date) cellValue, property.getDateFormat().value());
            } else {
                cell = cellValue.toString();
            }
            list.add(cell);
        }
        return list;

    }
}
