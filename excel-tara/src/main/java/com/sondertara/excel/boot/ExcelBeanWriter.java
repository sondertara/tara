package com.sondertara.excel.boot;

import com.sondertara.common.exception.TaraException;
import com.sondertara.excel.enums.ExcelDataType;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.parser.builder.DataCollectionBuilder;
import com.sondertara.excel.parser.builder.DateQueryBuilder;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * @author huangxiaohu
 */

public class ExcelBeanWriter extends AbstractExcelWriter<Workbook> {

    public static DateQueryBuilder<Workbook> fromQuery() {

        return new DateQueryBuilder<>(new ExcelBeanWriter());

    }

    public static DataCollectionBuilder<Workbook> fromData() {
        return new DataCollectionBuilder<>(new ExcelBeanWriter());
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

    @Override
    public Workbook generate() {
        // ExcelWriteSheetEntity excelMapping =
        // ExcelMappingFactory.loadExportExcelClass(excelClass);
        // ExcelWriterResolver resolver = new ExcelWriterResolver(excelMapping,
        // excelHelperBuilder.build());
        // if (this.multiSheet) {
        // return resolver.generateMultiSheetWorkbook(pageQueryParam, exportFunction);
        // } else {
        // return resolver.generateWorkbook(pageQueryParam, exportFunction);
        // }
        if (ExcelDataType.QUERY.equals(this.getExcelDataType())) {
            for (Map.Entry<Class<?>, ExportFunction<?>> entry : this.excelMapping.entrySet()) {
                this.getWriterContext().addMapper(entry.getKey(), entry.getValue(), this.pageQueryParam);
            }
        }
        return this.getWriterContext().getExecutor().execute();
    }
}
