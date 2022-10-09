package com.sondertara.excel.task;

/**
 * @author huangxiaohu
 *         消费
 */
public interface Consumer {
    /**
     * 消费任务
     *
     * @throws InterruptedException
     */
    void consume() throws InterruptedException;
}