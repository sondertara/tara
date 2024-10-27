package com.sondertara.excel.support.listener;

import com.sondertara.common.collection.Lists;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.excel.lifecycle.ExcelReadListener;
import com.sondertara.excel.meta.model.ExcelSheetDef;

import java.util.List;
import java.util.function.Consumer;

public class PageReadListener<T> implements ExcelReadListener<T> {
    /**
     * Single handle the amount of data;
     */
    public static int BATCH_COUNT = 200;

    private List<T> cachedDataList = Lists.newArrayListWithExpectedSize(BATCH_COUNT);

    private final Consumer<List<T>> consumer;

    public PageReadListener(Consumer<List<T>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void doAfterSheetAnalysed(ExcelSheetDef sheetDef) {
        if (CollectionUtils.isNotEmpty(cachedDataList)) {
            consumer.accept(cachedDataList);
        }
    }

    @Override
    public void doAfterRowAnalysed(T data) {
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            consumer.accept(cachedDataList);
            cachedDataList = Lists.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }
}
