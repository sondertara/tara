package com.sondertara.excel.parser;

import com.sondertara.common.exception.TaraException;
import com.sondertara.excel.entity.ExcelEntity;
import com.sondertara.excel.entity.ExcelHelper;
import com.sondertara.excel.entity.PageQueryParam;
import com.sondertara.excel.exception.ExcelTaraException;
import com.sondertara.excel.factory.ExcelMappingFactory;
import com.sondertara.excel.function.ExportFunction;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author huangxiaohu
 */

public class ExcelBeanWriter extends AbstractExcelWriter<Workbook> {


    private ExportFunction<?> exportFunction;

    private PageQueryParam pageQueryParam;
    private boolean multiSheet;
    private int sheetRowMaxCount;

    private final ExcelHelper.ExcelHelperBuilder excelHelperBuilder = ExcelHelper.builder();


    private ExcelBeanWriter(Class<?> excelClass) {
        super(excelClass);
    }


    public static ExcelBeanWriter mapper(Class<?> excelClass) {
        return new ExcelBeanWriter(excelClass);
    }


    public DateQueryBuilder fromQuery() {

        return new DateQueryBuilder(this);

    }

    public DataCollectionBuilder fromData() {
        return new DataCollectionBuilder(this);
    }


    public ExcelBeanWriter rowCacheSize(int rowCacheSize) {
        this.excelHelperBuilder.rowAccessWindowSize(rowCacheSize);
        return this;
    }

    public ExcelBeanWriter multiSheet(int sheetRowMaxCount) {
        this.multiSheet = true;
        excelHelperBuilder.recordCountPerSheet(sheetRowMaxCount);
        return this;
    }

    public ExcelBeanWriter autoColumWidth() {
        this.excelHelperBuilder.openAutoColumWidth(true);
        return this;
    }

    @Override
    public void to(OutputStream out) {

        try (Workbook workbook = generate()) {
            workbook.write(out);

        } catch (Exception e) {

            throw new TaraException("Write workbook to stream error", e);

        }

    }

    @Override
    public void to(HttpServletResponse httpServletResponse, String fileName) {
        try (Workbook wb = generate(); OutputStream out = httpServletResponse.getOutputStream()) {
            httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String s = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            httpServletResponse.setHeader("Content-disposition", "attachment; filename=\"" + s + "\"");
            if (null != out) {
                wb.write(out);
                out.flush();
            }
        } catch (Exception e) {
            throw new TaraException("Download Excel error", e);

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


    public void setExportFunction(ExportFunction<?> exportFunction) {
        this.exportFunction = exportFunction;
    }

    public PageQueryParam getPageQueryParam() {
        return pageQueryParam;
    }

    public void setPageQueryParam(PageQueryParam pageQueryParam) {
        this.pageQueryParam = pageQueryParam;
    }


    @Override
    public Workbook generate() {
        verifyAndBuildParams();
        ExcelEntity excelMapping = ExcelMappingFactory.loadExportExcelClass(excelClass);
        ExcelWriterResolver resolver = new ExcelWriterResolver(excelMapping, excelHelperBuilder.build());
        if (this.multiSheet) {
            return resolver.generateMultiSheetWorkbook(pageQueryParam, exportFunction);
        } else {
            return resolver.generateWorkbook(pageQueryParam, exportFunction);
        }
    }
}
