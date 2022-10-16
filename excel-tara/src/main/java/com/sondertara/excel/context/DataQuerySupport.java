package com.sondertara.excel.context;

import java.util.List;

public interface DataQuerySupport<T> {
    void afterQuery(int index ,List<T> result);
}
