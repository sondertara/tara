package com.sondertara.excel.task;

import com.sondertara.common.model.PageResult;
import com.sondertara.common.struct.weapper.Wrapper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/8/14 9:47
 */
public class PageResultWrapper<T> implements Wrapper<PageResult<T>>, PageVisitor {
    private final PageResult<T> pageResult;
    private final AtomicInteger currentPage;

    public PageResultWrapper(PageResult<T> pageResult, AtomicInteger currentPage) {
        this.pageResult = pageResult;
        this.currentPage = currentPage;
    }


    @Override
    public PageResult<T> getRaw() {
        return pageResult;
    }

    @Override
    public int maxIndex() {
        return pageResult.endIndex();
    }

    @Override
    public int currentIndex() {
        return pageResult.getPage();
    }

    @Override
    public int visitedIndex() {
        return currentPage.get();
    }
}
