package com.sondertara.common.model;

import com.sondertara.common.bean.BeanUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.reflect.ReflectUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页结果
 *
 * @author huangxiaohu
 * @date 2021/7/21 12:43
 */
@Data
public class PageResult<T> implements Serializable {

    private Integer page;
    private Integer pageSize;
    private Long total;
    private List<T> data;

    protected PageResult(List<T> data) {
        if (null == data) {
            this.data = new ArrayList<>();
        } else {
            this.data = data;
        }
    }

    public PageResult(List<T> data, Long total, Integer page, Integer pageSize) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.data = data;
    }

    public static <T> Builder<T> of(List<T> data) {
        return new Builder<>(data);
    }


    public static class Builder<T> {

        private final PageResult<T> pageResult;

        public Builder(List<T> data) {
            this.pageResult = new PageResult<>(data);
        }

        public Builder<T> pagination(Integer page, Integer pageSize) {
            pageResult.setPage(page);
            pageResult.setPageSize(pageSize);
            return this;
        }

        public Builder<T> total(Long total) {
            pageResult.setTotal(total);
            return this;
        }

        public PageResult<T> build() {
            return pageResult;
        }
    }


    public static <T, R> PageResult<R> copy(PageResult<T> source, Class<R> tClass) {
        List<R> targetPageData = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(source.getData())) {

            for (T data : source.getData()) {
                R target = ReflectUtils.newInstance(tClass);
                BeanUtils.copyProperties(data, target);
                targetPageData.add(target);
            }
        }

        return new PageResult<>(targetPageData, source.getTotal(), source.getPage(), source.getPageSize());

    }

    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    public int endIndex() {
        return (int) Math.ceil(this.total * 1.0f / pageSize) - 1;
    }


}
