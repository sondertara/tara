package com.sondertara.excel.support.converter;

import com.sondertara.common.util.StringUtils;
import com.sondertara.excel.exception.ExcelConvertException;
import com.sondertara.excel.meta.annotation.converter.ExcelKVConvert;


import java.util.HashMap;
import java.util.Map;

/**
 * @author huangxiaohu
 */
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
            return this.kvMap.get(value);
        } else {
            if (!this.allowMissHit) {
                throw new ExcelConvertException("value [{}] miss hit! allow value:{}", value, this.kvMap.keySet());
            }
        }
        return value;
    }
}
