package com.sondertara.excel.support.converter;

import com.sondertara.excel.exception.ExcelException;
import com.sondertara.excel.meta.annotation.converter.ExcelKVConvert;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ExcelKVConverter implements AbstractExcelColumnConverter<ExcelKVConvert, Object> {

    private Map<Object, Object> kvMap;

    private boolean allowMissHit;

    @Override
    public void initialize(ExcelKVConvert annotation) {
        this.allowMissHit = annotation.allowMissHit();
        String[] kvmap = annotation.kvmap();
        this.kvMap = new HashMap<>();
        for (String kv : kvmap) {
            String[] aKv = StringUtils.split(kv, "=");
            if (aKv == null || aKv.length != 2) {
                throw new IllegalArgumentException("@ExcelKVConvert's kvmap attributes must include \"=\"");
            }
            this.kvMap.put(aKv[0], aKv[1]);
        }

    }

    @Override
    public Object convert(String value) {
        if (this.kvMap.containsKey(value)) {
            return MapUtils.getObject(this.kvMap, value);
        } else {
            if (!this.allowMissHit) {
                throw new ExcelException("value [" + value + "] miss hit! allow value:" + this.kvMap.keySet());
            }
        }
        return value;
    }
}
