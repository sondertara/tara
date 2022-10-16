package com.sondertara.excel.task;

/**
 * @author huangxiaohu
 * 生产
 */
public interface Producer {
    /**
     * 生产任务
     */
    boolean produce();

    void init();
}
