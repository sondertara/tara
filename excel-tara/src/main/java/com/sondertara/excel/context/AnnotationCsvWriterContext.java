package com.sondertara.excel.context;

import com.sondertara.excel.executor.ExcelCsvWriterExecutor;
import com.sondertara.excel.executor.TaraExcelExecutor;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author huangxiaohu
 */
public class AnnotationCsvWriterContext extends BaseAnnotationExcelWriterContext<Path> {


    private final AtomicReference<Path> reference = new AtomicReference<>();

    @Override
    public Path getResult() {
        if (null == reference.get()) {
            getExecutor().execute();
        }
        return reference.get();
    }

    @Override
    public TaraExcelExecutor getExecutor() {
        return new ExcelCsvWriterExecutor(reference, this.getSheetDefinitions());
    }
}
