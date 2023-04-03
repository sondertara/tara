package com.sondertara.excel.task;

/**
 * @author huangxiaohu
 * 生产
 */
public interface Producer {
    /**
     * 生产任务
     * @return if the producer is done
     */
    boolean produce();

    /**
     * init
     */
    void init();

    void  exit();
}
