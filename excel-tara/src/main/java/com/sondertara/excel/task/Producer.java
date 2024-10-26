package com.sondertara.excel.task;

/**
 * @author huangxiaohu
 * 生产
 */
public interface Producer extends Runnable{
    /**
     * 生产任务
     * @return if the producer is done
     */
    void produce();

    /**
     * init
     */
    void init();

    void  exit();
}
