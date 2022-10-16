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
        String[] kvMap1 = annotation.kvMap();
        this.kvMap = new HashMap<>(8);
        for (String kv : kvMap1) {
            String[] aKv = StringUtils.split(kv, "=");
            if (aKv == null || aKv.length != 2) {
                throw new IllegalArgumentException("@ExcelKVConvert's kvMap attributes must include \"=\"");
            }
            this.kvMap.put(aKv[0], aKv[1]);
        }

    }

    @Override
    public Object convert(Object value) {
        if (value == null) {
            return null;
        }
        if (this.kvMap.containsKey(value.toString())) {
            return MapUtils.getObject(this.kvMap, value);
        } else {
            if (!this.allowMissHit) {
                throw new ExcelException("value [" + value + "] miss hit! allow value:" + this.kvMap.keySet());
            }
        }
        return value;
    }
}
