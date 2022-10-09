package com.sondertara.excel.meta.celltype;

import com.sondertara.common.util.StringUtils;
import com.sondertara.excel.constants.ExcelConstants;
import com.sondertara.excel.utils.ExcelXmlCodecUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.Attributes;

/**
 * 数值类型
 */
@Slf4j
public class ExcelNumberCellType implements ExcelCellType {

    private static final long serialVersionUID = -3254659667807688559L;
    private StylesTable stylesTable;

    public ExcelNumberCellType(final StylesTable stylesTable) {
        this.stylesTable = stylesTable;
    }

    @Override
    public boolean matches(final String name, final Attributes attributes) {
        if (ExcelConstants.CELL_TAG.equals(name)) {
            // s不为空
            if (!StringUtils.isBlank(attributes.getValue(ExcelConstants.CELL_STYLE_ATTR))) {

                final String dataFormat = ExcelXmlCodecUtils.getDataFormat(
                        Integer.parseInt(attributes.getValue(ExcelConstants.CELL_STYLE_ATTR)), this.stylesTable);
                if (StringUtils.containsAny(dataFormat, "#", "General")) {
                    if (log.isDebugEnabled()) {
                        log.debug("The [{} - {}] matches [{}]!", getCellAttributes(attributes), dataFormat,
                                this.getClass().getName());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getValue(final String value) {
        return value;
    }
}