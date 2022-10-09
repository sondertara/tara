package com.sondertara.excel.meta.celltype;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 单元格格式
 *
 * @author huangxiaohu
 */
public interface ExcelCellType extends Serializable {

    boolean matches(String name, Attributes attributes);

    String getValue(String value);

    default String getCellAttributes(final Attributes attributes) {
        final int length = attributes.getLength();
        final List<String> attrs = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            attrs.add(attributes.getQName(i) + "=" + attributes.getValue(i));
        }
        return StringUtils.join(attrs);
    }
}