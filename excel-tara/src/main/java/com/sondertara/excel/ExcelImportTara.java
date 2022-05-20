package com.sondertara.excel;


import com.sondertara.excel.annotation.ExcelExportField;
import com.sondertara.excel.entity.ExcelEntity;
import com.sondertara.excel.exception.ExcelTaraException;
import com.sondertara.excel.factory.ExcelMappingFactory;
import com.sondertara.excel.parser.ExcelReadHandler;
import com.sondertara.excel.parser.ExcelReader;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Excel kit.  use this kit you need work with the builder, avoid using the constructor
 *
 * @author huangxiaohu
 */
public class ExcelImportTara {

    private static final Logger logger = LoggerFactory.getLogger(ExcelImportTara.class);
    /**
     * the class to work
     * <p>
     * {@link ExcelExportField}
     * <p>
     * {@link ExcelExportField}
     */
    private Class<?> excelClass;

    private boolean enableIndex = false;


    protected ExcelImportTara() {
    }


    protected ExcelImportTara(Class<?> clazz) {
        this.excelClass = clazz;
    }


    public static ExcelImportTara mapper(Class<?> excelClass) {
        return new ExcelImportTara(excelClass);
    }


    public ExcelImportTara enableIndex(boolean enableIndex) {
        this.enableIndex = enableIndex;
        return this;
    }


    /**
     * import all Excel sheet
     */
    public ExcelReadHandler from(InputStream inputStream) {
        try {
            if (inputStream == null) {
                throw new ExcelTaraException("inputStream is null");
            }
            ExcelEntity excelMapping = ExcelMappingFactory.loadImportExcelClass(excelClass);
            ExcelReader excelReader = new ExcelReader(excelClass, excelMapping, 1, enableIndex);
            return new ExcelReadHandler(excelReader, inputStream);
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }

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
    }
}