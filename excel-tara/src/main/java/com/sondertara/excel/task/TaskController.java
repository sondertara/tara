package com.sondertara.excel.task;

/**
 * @author huangxiaohu
 */
public interface TaskController {
    /**
     * set consumers count
     *
     * @param threadNum the number of threads
     */
    void consumers(int threadNum);

    /**
     * set producer count
     *
     * @param threadNum the number of threads
     */
    void producers(int threadNum);

    void start();
}
