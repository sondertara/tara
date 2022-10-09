package com.sondertara.excel.analysis;

public interface LifecycleSupport {

    public void beforeParseSheet(int sheetIndex);

    public void afterParseSheet(int sheetIndex);

    public void finish();
}
