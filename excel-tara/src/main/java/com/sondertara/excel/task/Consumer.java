package com.sondertara.excel.task;

/**
 * @author huangxiaohu
 * 消费
 */
public interface Consumer extends Runnable {

    void init();

    /**
     * 消费任务
     *
     */
    void consume();

    void exit();
}