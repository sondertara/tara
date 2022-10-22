package com.sondertara.excel.antlr.tablemodel;

import lombok.Data;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.ReadingOrder;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.io.Serializable;

/**
 * @author huangxiaohu
 */
@Data
public class CellStyle implements Serializable {

    private static final long serialVersionUID = 6572274827392389515L;

    private BorderStyle borderTopEnum;
    private BorderStyle borderBottomEnum;
    private BorderStyle borderLeftEnum;
    private BorderStyle borderRightEnum;

    private short topBorderColor;
    private short bottomBorderColor;
    private short leftBorderColor;
    private short rightBorderColor;

    private XSSFColor fillBackgroundXSSFColor;
    private XSSFColor fillForegroundXSSFColor;
    private FillPatternType fillPattern;

    private short dataFormat;
    private Boolean hidden;
    private Boolean locked;

    private short indention;
    private Boolean wrapText;
    private Boolean shrinkToFit;
    private ReadingOrder readingOrder;
    private Boolean quotePrefixed;
    private short rotation;

    private HorizontalAlignment alignmentEnum;
    private VerticalAlignment verticalAlignmentEnum;

    private Font font;

    public CellStyle(XSSFCellStyle xssfCellStyle) {
        this.borderTopEnum = xssfCellStyle.getBorderTop();
        this.borderBottomEnum = xssfCellStyle.getBorderBottom();
        this.borderLeftEnum = xssfCellStyle.getBorderLeft();
        this.borderRightEnum = xssfCellStyle.getBorderRight();
        this.topBorderColor = xssfCellStyle.getTopBorderColor();
        this.bottomBorderColor = xssfCellStyle.getBottomBorderColor();
        this.leftBorderColor = xssfCellStyle.getLeftBorderColor();
        this.rightBorderColor = xssfCellStyle.getRightBorderColor();
        this.fillBackgroundXSSFColor = xssfCellStyle.getFillBackgroundXSSFColor();
        this.fillForegroundXSSFColor = xssfCellStyle.getFillForegroundXSSFColor();
        this.fillPattern = xssfCellStyle.getFillPattern();
        this.dataFormat = xssfCellStyle.getDataFormat();
        this.hidden = xssfCellStyle.getHidden();
        this.locked = xssfCellStyle.getLocked();
        this.indention = xssfCellStyle.getIndention();
        this.wrapText = xssfCellStyle.getWrapText();
        this.shrinkToFit = xssfCellStyle.getShrinkToFit();
        this.readingOrder = xssfCellStyle.getReadingOrder();
        this.quotePrefixed = xssfCellStyle.getQuotePrefixed();
        this.rotation = xssfCellStyle.getRotation();
        this.alignmentEnum = xssfCellStyle.getAlignment();
        this.verticalAlignmentEnum = xssfCellStyle.getVerticalAlignment();
        this.font = new Font(xssfCellStyle.getFont());
    }

}
