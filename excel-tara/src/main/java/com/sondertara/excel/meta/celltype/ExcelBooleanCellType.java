package com.sondertara.excel.meta.celltype;

import com.sondertara.excel.common.constants.ExcelConstants;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;

/**
 * 布尔值类型（1=true; 0=false）
 */

@Slf4j
public class ExcelBooleanCellType implements ExcelCellType {

    private static final long serialVersionUID = -3049964850218918322L;

    @Override
    public boolean matches(final String name, final Attributes attributes) {
        if (ExcelConstants.CELL_TAG.equals(name)) {
            // t = "b"
            if (ExcelConstants.CELL_BOOLEAN_TYPE.equals(attributes.getValue(ExcelConstants.CELL_TYPE_ATTR))) {
                if (log.isDebugEnabled()) {
                    log.debug("The [{}] matches [{}]", getCellAttributes(attributes), this.getClass().getName());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public String getValue(final String value) {
        return value;
    }
}