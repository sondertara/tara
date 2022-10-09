package com.sondertara.excel.meta.celltype;

import com.sondertara.common.util.StringUtils;
import com.sondertara.excel.constants.ExcelConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.xml.sax.Attributes;

import java.util.Date;

/**
 * 日期格式
 */
@Slf4j
public class ExcelDateCellType implements ExcelCellType {

    private static final long serialVersionUID = -9216501976104636864L;
    private final StylesTable stylesTable;

    public ExcelDateCellType(final StylesTable stylesTable) {
        this.stylesTable = stylesTable;
    }

    @Override
    public boolean matches(final String name, final Attributes attributes) {

        if (ExcelConstants.CELL_TAG.equals(name)) {
            // s不为空
            if (!StringUtils.isBlank(attributes.getValue(ExcelConstants.CELL_STYLE_ATTR))) {
                final int styleIndex = Integer.parseInt(attributes.getValue(ExcelConstants.CELL_STYLE_ATTR));
                final XSSFCellStyle cellStyle = this.stylesTable.getStyleAt(styleIndex);
                final String dataFormatString = cellStyle.getDataFormatString();
                if (StringUtils.containsAny(dataFormatString, "y", "m", "d", "h", "s", "Y", "M", "D")) {
                    if (log.isDebugEnabled()) {
                        log.debug("The [{} - {}] matches [{}]!", getCellAttributes(attributes), dataFormatString,
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
        final Date date = DateUtil.getJavaDate(Double.parseDouble(value));
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }
}