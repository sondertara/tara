package com.sondertara.excel.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ListUtils {

    /**
     * 将列表切割成指定大小
     * 
     * @param data
     * @param rowsPerSegment
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> split(List<T> data, Integer rowsPerSegment) {
        if (data.size() > rowsPerSegment) {
            int dataSize = data.size();
            int segmentCount = dataSize / rowsPerSegment;
            if (dataSize % rowsPerSegment > 0) {
                segmentCount++;
            }
            List<List<T>> retList = new ArrayList<>();
            for (int i = 0; i < segmentCount; i++) {
                int startIndex = i * rowsPerSegment;
                int endIndex = (i + 1) * rowsPerSegment;
                if (endIndex > dataSize) {
                    endIndex = dataSize;
                }
                retList.add(data.subList(startIndex, endIndex));
            }
            return retList;
        } else {
            return Collections.singletonList(data);
        }
    }

    /**
     * 获取泛型类型
     * 
     * @param list
     * @return
     */
    public static Class<?> getGenericClass(List<?> list) {
        if (list.size() == 0) {
            throw new IllegalArgumentException("collection size == 0");
        }
        return list.get(0).getClass();
    }

}
