package com.sondertara.excel.analysis;

public interface LifecycleSupport {

    void beforeParseSheet(int sheetIndex);

    void afterParseSheet(int sheetIndex);

    void finish();
}
