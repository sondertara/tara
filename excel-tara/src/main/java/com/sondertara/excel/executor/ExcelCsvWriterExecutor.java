package com.sondertara.excel.executor;

import com.sondertara.common.id.NanoId;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.excel.entity.ExcelWriteSheetEntity;
import com.sondertara.excel.exception.ExcelTaraException;
import com.sondertara.excel.exception.ExcelWriterException;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.meta.model.AnnotationSheet;
import com.sondertara.excel.meta.support.ExcelMappingFactory;
import com.sondertara.excel.resolver.ExcelCsvWriterResolver;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author huangxiaohu
 */
@Slf4j
public class ExcelCsvWriterExecutor implements TaraExcelExecutor{


    Map<String,AnnotationSheet> sheetDefinitions;
    private final AtomicReference<Path> filepath;

    public ExcelCsvWriterExecutor(AtomicReference<Path> filepath,Map<String, AnnotationSheet> sheetDefinitions) {
        this.sheetDefinitions = sheetDefinitions;
        this.filepath = filepath;
    }

    @Override
    public void execute() {
        log.info("CSV exporting is starting...");
        try {
            if (CollectionUtils.isEmpty(sheetDefinitions)) {
                throw new ExcelWriterException("The sheet definition must be not null");
            }
            AnnotationSheet annotationSheet = sheetDefinitions.values().stream().findFirst().orElseThrow(() -> new ExcelWriterException("The sheet definition must be not null"));
            ExportFunction<?> exportFunction = annotationSheet.getQueryFunction();
            Class<?> mappingClass = annotationSheet.getMappingClass();
            ExcelWriteSheetEntity excelMapping = ExcelMappingFactory.loadExportExcelClass(mappingClass);
            ExcelCsvWriterResolver resolver = new ExcelCsvWriterResolver(excelMapping, NanoId.randomNanoId(16));
           this.filepath.set(resolver.createFile(exportFunction));
        } catch (Exception e) {
            throw new ExcelTaraException(e);
        }
    }
}
