package com.sondertara.excel.task;

/**
 * @author huangxiaohu
 *         生产
 */
public interface Producer {
    /**
     * 生产任务
     *
     * @throws InterruptedException 异常信息
     */
    void produce() throws InterruptedException;
}
