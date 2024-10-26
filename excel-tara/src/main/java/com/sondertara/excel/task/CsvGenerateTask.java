package com.sondertara.excel.task;

import com.sondertara.common.datetime.LocalDateTimeUtils;
import com.sondertara.common.text.StringUtils;
import com.sondertara.excel.base.TaraExcelConfig;
import com.sondertara.excel.common.constants.Constants;
import com.sondertara.excel.entity.ExcelCellEntity;
import com.sondertara.excel.entity.ExcelWriteSheetEntity;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.meta.annotation.ExcelDataFormat;
import de.siegmar.fastcsv.writer.CsvWriter;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author huangxiaohu
 */
public class CsvGenerateTask<R> extends AbstractExcelGenerateTask<R> {
    private static final Logger logger = LoggerFactory.getLogger(CsvGenerateTask.class);
    private final ExcelWriteSheetEntity excelEntity;
    private final String filename;

    public CsvGenerateTask(ExportFunction<R> exportFunction, ExcelWriteSheetEntity e, String filename) {
        super(exportFunction);
        this.excelEntity = e;
        this.filename = filename;
        this.consumers(TaraExcelConfig.CONFIG.getCsvConsumerThread());
        this.producers(TaraExcelConfig.CONFIG.getExcelProducerThread());
    }

    @Override
    public void consumers(int threadNum) {
        super.consumers(threadNum);
    }

    @Override
    public void producers(int threadNum) {
        super.producers(threadNum);
    }

    @Override
    protected void consumeData(@NonNull PageResultWrapper<R> data) {

        int currentIndex = data.currentIndex();
        if (logger.isDebugEnabled()) {
            logger.debug("Data of page[{}] processing  is starting ......", currentIndex);
        }
        try {
            final String workPath = Constants.FILE_PATH + File.separator + filename + File.separator;
            File file = new File(workPath);
            if (!file.exists()) {
                boolean mkdir = file.mkdirs();
                if (!mkdir) {
                    throw new IOException("Create directory:" + file.getAbsolutePath() + " error");
                }
            }
            PrintWriter printWriter = new PrintWriter(workPath + currentIndex + ".csv", Constants.CHARSET);

            try (CsvWriter csv = CsvWriter.builder().build(printWriter)) {
                final List<R> list = data.getRaw().getData();
                for (R pageData : list) {
                    List<String> row = buildRow(pageData, excelEntity);
                    csv.writeRow(row);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Data of page[{}] processing has been completed...", currentIndex);
            }
        } catch (Exception e) {
            logger.error("write into file error:", e);
        }
    }

    /**
     * build data row except first row in Excel.
     *
     * @param entity      data
     * @param excelEntity excel entity via {@link com.sondertara.excel.meta.annotation.ExcelExportField}
     */
    private List<String> buildRow(Object entity, ExcelWriteSheetEntity excelEntity) throws IllegalAccessException {

        List<ExcelCellEntity> propertyList = excelEntity.getPropertyList();
        List<String> list = new ArrayList<>(propertyList.size());
        for (ExcelCellEntity property : propertyList) {
            String cell = null;
            Field field = property.getFieldEntity();
            Object cellValue = field.get(entity);

            if (cellValue == null) {
                cell = "";
            } else if (cellValue instanceof BigDecimal) {
                cell = (((BigDecimal) cellValue).setScale(property.getScale(), property.getRoundingMode())).toString();
            } else if (cellValue instanceof Date) {
                ExcelDataFormat dateFormat = property.getDateFormat();
                if (null == dateFormat || StringUtils.isBlank(dateFormat)) {
                    cell = LocalDateTimeUtils.format((Date) cellValue);
                }
            } else if (cellValue instanceof LocalDate) {
                ExcelDataFormat dateFormat = property.getDateFormat();
                if (null == dateFormat || StringUtils.isBlank(dateFormat)) {
                    cell = LocalDateTimeUtils.format((LocalDate) cellValue);
                }
            } else if (cellValue instanceof LocalDateTime) {
                ExcelDataFormat dateFormat = property.getDateFormat();
                if (null == dateFormat || StringUtils.isBlank(dateFormat)) {
                    cell = LocalDateTimeUtils.format((LocalDateTime) cellValue);
                }
            } else {
                cell = cellValue.toString();
            }
            list.add(cell);
        }
        return list;

    }
}
