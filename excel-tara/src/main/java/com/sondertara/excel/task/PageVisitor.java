package com.sondertara.excel.task;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/8/14 10:25
 */
public interface PageVisitor extends Delayed {
    int maxIndex();

    int currentIndex();

    int visitedIndex();

    @Override
    default long getDelay(TimeUnit unit) {
        return currentIndex() - visitedIndex();
    }

    @Override
    default int compareTo(Delayed o) {
        return Integer.compare(currentIndex(), ((PageVisitor) o).currentIndex());
    }
}
