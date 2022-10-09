package com.sondertara.excel.meta.celltype;

import com.sondertara.excel.constants.ExcelConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;

/**
 * 内联字符串（不使用共享池）
 */

@Slf4j
public class ExcelInlineStrCellType implements ExcelCellType {

    private static final long serialVersionUID = 9115672114160103097L;

    @Override
    public boolean matches(final String name, final Attributes attributes) {

        if (ExcelConstants.CELL_TAG.equals(name)) {
            // t="inlinStr"
            if (ExcelConstants.CELL_INLINE_STR_TYPE.equals(attributes.getValue(ExcelConstants.CELL_TYPE_ATTR))) {
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
        final XSSFRichTextString rtsi = new XSSFRichTextString(value);
        return rtsi.toString();
    }
}