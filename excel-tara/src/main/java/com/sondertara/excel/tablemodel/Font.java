package com.sondertara.excel.tablemodel;

import lombok.Data;
import org.apache.poi.ss.usermodel.FontScheme;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.io.Serializable;

/**
 * @author Chimm Huang
 */
@Data
public class Font implements Serializable {

    private static final long serialVersionUID = 6517940000596931192L;

    private Boolean bold;
    private int charSet;
    private short color;
    private int family;
    private short fontHeight;
    private short fontHeightInPoints;
    private String fontName;
    private Boolean italic;
    private FontScheme scheme;
    private Boolean strikeout;
    private short themeColor;
    private short typeOffset;
    private byte underline;

    public Font(XSSFFont xssfFont) {
        this.bold = xssfFont.getBold();
        this.charSet = xssfFont.getCharSet();
        this.color = xssfFont.getColor();
        this.family = xssfFont.getFamily();
        this.fontHeight = xssfFont.getFontHeight();
        this.fontHeightInPoints = xssfFont.getFontHeightInPoints();
        this.fontName = xssfFont.getFontName();
        this.italic = xssfFont.getItalic();
        this.scheme = xssfFont.getScheme();
        this.strikeout = xssfFont.getStrikeout();
        this.themeColor = xssfFont.getThemeColor();
        this.typeOffset = xssfFont.getTypeOffset();
        this.underline = xssfFont.getUnderline();
    }


}
