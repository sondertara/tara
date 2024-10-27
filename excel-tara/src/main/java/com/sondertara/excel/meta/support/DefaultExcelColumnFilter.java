package com.sondertara.excel.meta.support;

import java.util.List;

/**
 * filter the export columns
 * @author huangxiaohu
 */
public class DefaultExcelColumnFilter implements ExcelColumnFilter {
    @Override
    public List<String> apply(List<String> origin) {
        return origin;
    }
}
