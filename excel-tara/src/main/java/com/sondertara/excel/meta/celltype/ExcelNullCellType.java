package com.sondertara.excel.meta.celltype;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;

@Slf4j
public class ExcelNullCellType implements ExcelCellType {

    private static final long serialVersionUID = 1030011032019218508L;

    @Override
    public boolean matches(final String name, final Attributes attributes) {
        if (log.isDebugEnabled()) {
            log.debug("The [{}] matches [{}]", getCellAttributes(attributes), this.getClass().getName());
        }
        return true;
    }

    @Override
    public String getValue(final String value) {
        return value;
    }
}