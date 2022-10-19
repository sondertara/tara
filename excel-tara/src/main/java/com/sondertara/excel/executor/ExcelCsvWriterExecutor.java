package com.sondertara.excel.executor;

import com.sondertara.common.lang.id.NanoId;
import com.sondertara.common.util.CollectionUtils;
import com.sondertara.excel.entity.ExcelWriteSheetEntity;
import com.sondertara.excel.exception.ExcelTaraException;
import com.sondertara.excel.exception.ExcelWriterException;
import com.sondertara.excel.factory.ExcelMappingFactory;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.meta.model.AnnotationSheet;
import com.sondertara.excel.parser.ExcelCsvWriterResolver;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author huangxiaohu
 */
@Slf4j
public class ExcelCsvWriterExecutor implements TaraExcelExecutor<String> {


    Map<String,AnnotationSheet> sheetDefinitions;
    private final String filename;

    public ExcelCsvWriterExecutor(Map<String,AnnotationSheet> sheetDefinitions) {
        this.sheetDefinitions = sheetDefinitions;
        this.filename = NanoId.randomNanoId(16);
    }

    @Override
    public String execute() {
        log.info("CSV exporting is starting...");
        try {
            if (CollectionUtils.isEmpty(sheetDefinitions)) {
                throw new ExcelWriterException("The sheet definition must be not null");
            }
            AnnotationSheet annotationSheet = sheetDefinitions.values().stream().findFirst().orElseThrow(() -> new ExcelWriterException("The sheet definition must be not null"));
            ExportFunction<?> exportFunction = annotationSheet.getQueryFunction();
            Class<?> mappingClass = annotationSheet.getMappingClass();
            ExcelWriteSheetEntity excelMapping = ExcelMappingFactory.loadExportExcelClass(mappingClass);
            ExcelCsvWriterResolver resolver = new ExcelCsvWriterResolver(excelMapping, filename);
            return resolver.createFile(exportFunction);
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }
}
