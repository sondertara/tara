package com.sondertara.excel.parser;


import com.sondertara.excel.entity.ExcelEntity;
import com.sondertara.excel.exception.ExcelTaraException;
import com.sondertara.excel.factory.ExcelMappingFactory;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * excel write
 *
 * @author huangxiaohu
 */
public class ExcelCsvWriter extends AbstractExcelWriter<String> {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);


    private Integer nullCellCount = 0;
    private XSSFCellStyle headCellStyle;
    private final Map<Integer, Integer> columnWidthMap = new HashMap<>();
    private String fileName;

    public static ExcelCsvWriter mapper(Class<?> excelClass) {
        return new ExcelCsvWriter(excelClass);
    }

    private ExcelCsvWriter(Class<?> excelClass) {
        this.excelClass = excelClass;
    }

    public DateQueryBuilder fromQuery() {

        return new DateQueryBuilder(this);

    }

    public ExcelCsvWriter fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public DataCollectionBuilder fromData() {
        return new DataCollectionBuilder(this);
    }


    /**
     * validate param and set default value when some field is null.
     */
    private void verifyAndBuildParams() {
        if (excelClass == null) {
            throw new ExcelTaraException("param excelClass is null");
        }
    }

    @Override
    public String generate() {
        logger.info("CSV exporting is starting...");
        try {
            verifyAndBuildParams();
            ExcelEntity excelMapping = ExcelMappingFactory.loadExportExcelClass(excelClass);
            ExcelCsvWriterResolver resolver = new ExcelCsvWriterResolver(excelMapping, fileName);
            return resolver.createFile(pageQueryParam, exportFunction);
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }

    @Override
    public void to(OutputStream out) {

    }

    @Override
    public void to(HttpServletResponse httpServletResponse, String fileName) {

    }
}
