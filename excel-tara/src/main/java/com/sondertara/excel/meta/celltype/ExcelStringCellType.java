package com.sondertara.excel.meta.celltype;

import com.sondertara.excel.constants.ExcelConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;

/**
 * 共享字符串格式
 */
@Slf4j
public class ExcelStringCellType implements ExcelCellType {

    private static final long serialVersionUID = 1368517956940674679L;

    private final SharedStrings sst;

    public ExcelStringCellType(final SharedStrings sst) {
        this.sst = sst;
    }

    @Override
    public boolean matches(final String name, final Attributes attributes) {

        if (ExcelConstants.CELL_TAG.equals(name)) {
            // 字符串类型 t="s"
            if (ExcelConstants.CELL_STRING_TYPE.equals(attributes.getValue(ExcelConstants.CELL_TYPE_ATTR))) {
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
        final int idx = Integer.parseInt(value);
        return new XSSFRichTextString(this.sst.getItemAt(idx).getString()).toString();
    }
}