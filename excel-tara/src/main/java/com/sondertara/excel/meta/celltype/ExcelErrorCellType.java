package com.sondertara.excel.meta.celltype;

import com.sondertara.excel.constants.ExcelConstants;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;

/**
 * 错误类型
 */
@Slf4j
public class ExcelErrorCellType implements ExcelCellType {

    private static final long serialVersionUID = 9033391964718310293L;

    @Override
    public boolean matches(final String name, final Attributes attributes) {
        if (ExcelConstants.CELL_TAG.equals(name)) {
            // t = "e"
            if (ExcelConstants.CELL_ERROR_TYPE.equals(attributes.getValue(ExcelConstants.CELL_TYPE_ATTR))) {
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