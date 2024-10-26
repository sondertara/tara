package com.sondertara.excel.task;

import com.sondertara.excel.function.ExportFunction;
import org.jspecify.annotations.NonNull;

/**
 * generate csv excel file
 *
 * @param <R> query result
 * @author huangxiaohu
 */
public abstract class AbstractExcelGenerateTask<R> extends AbstractTaskExecutor<PageResultWrapper<R>> {

    private final ExportFunction<R> exportFunction;


    public AbstractExcelGenerateTask(ExportFunction<R> exportFunction) {
        this.exportFunction = exportFunction;
    }
    @Override
    protected @NonNull PageResultWrapper<R> produceData(int index) {
        return new PageResultWrapper<>(exportFunction.query(index), getVisitedIndex());
    }

}
