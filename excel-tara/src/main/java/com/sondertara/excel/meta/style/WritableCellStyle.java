package com.sondertara.excel.meta.style;

import com.sondertara.excel.meta.style.border.CellBorder;
import com.sondertara.excel.meta.style.border.CellBorders;
import com.sondertara.excel.meta.style.color.StyleColor;
import com.sondertara.excel.meta.style.font.StyleFont;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.util.Objects;

/**
 * Contains style-related properties to be applied on cells in the sheet.
 */
public class WritableCellStyle {

    private final String format;
    private final HorizontalAlignment horizontalAlignment;
    private final VerticalAlignment verticalAlignment;
    private final CellBorder border;
    private final StyleColor backgroundColor;
    private final StyleFont font;
    private final boolean wrapText;

    private WritableCellStyle(CellStyleBuilder builder) {
        format = builder.format;
        horizontalAlignment = builder.horizontalAlignment;
        verticalAlignment = builder.verticalAlignment;
        border = builder.border;
        backgroundColor = builder.backgroundColor;
        font = builder.font;
        wrapText = builder.wrapText;
    }

    public String getFormat() {
        return format;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public CellBorder getBorder() {
        return border;
    }

    public StyleColor getBackgroundColor() {
        return backgroundColor;
    }

    public StyleFont getFont() {
        return font;
    }

    public boolean isWrapText() {
        return wrapText;
    }

    /**
     * Combines the properties of this cell style with the properties of another one into a new style.
     * Defined properties have priority over non-defined properties, except boolean properties, where "true" has priority.
     * The properties of this style have priority over the other style.
     *
     * @param other another style.
     * @return a new style, with properties combined.
     */
    public WritableCellStyle combineWith(WritableCellStyle other) {
        return WritableCellStyle.builder()
                .format(!StringUtils.isEmpty(format) ? format : other.format)
                .horizontalAlignment(horizontalAlignment != null ? horizontalAlignment : other.horizontalAlignment)
                .verticalAlignment(verticalAlignment != null ? verticalAlignment : other.verticalAlignment)
                .border(combinedBorder(other.border))
                .backgroundColor(backgroundColor != null ? backgroundColor : other.backgroundColor)
                .font(combinedFont(other.font))
                .wrapText(wrapText || other.wrapText)
                .build();
    }

    private CellBorder combinedBorder(CellBorder other) {
        if (this.border == null) {
            return other;
        }
        if (other == null) {
            return this.border;
        }
        return border.combineWith(other);
    }

    private StyleFont combinedFont(StyleFont other) {
        if (this.font == null) {
            return other;
        }
        if (other == null) {
            return this.font;
        }
        return font.combineWith(other);
    }

    public static CellStyleBuilder builder() {
        return new CellStyleBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WritableCellStyle cellStyle = (WritableCellStyle) o;
        return wrapText == cellStyle.wrapText
                && horizontalAlignment == cellStyle.horizontalAlignment
                && verticalAlignment == cellStyle.verticalAlignment
                && Objects.equals(format, cellStyle.format)
                && Objects.equals(border, cellStyle.border)
                && Objects.equals(backgroundColor, cellStyle.backgroundColor)
                && Objects.equals(font, cellStyle.font);
    }

    @Override
    public int hashCode() {
        return Objects.hash(format, horizontalAlignment, verticalAlignment, border, backgroundColor, font, wrapText);
    }

    public static final class CellStyleBuilder {

        private String format;
        private HorizontalAlignment horizontalAlignment;
        private VerticalAlignment verticalAlignment;
        private CellBorder border;
        private StyleColor backgroundColor;
        private StyleFont font;
        private boolean wrapText;

        /**
         * Sets the cell number format. In Excel, this corresponds to: "Home" tab > "Number" section > In the dropdown - "More number
         * formats" > Custom.
         *
         * @param format the number format of the cell.
         */
        public CellStyleBuilder format(String format) {
            this.format = format;
            return this;
        }

        /**
         * Sets the text horizontal alignment within the cell.
         *
         * @param horizontalAlignment text alignment.
         */
        public CellStyleBuilder horizontalAlignment(HorizontalAlignment horizontalAlignment) {
            this.horizontalAlignment = horizontalAlignment;
            return this;
        }

        /**
         * Sets the text vertical alignment within the cell.
         *
         * @param verticalAlignment text alignment.
         */
        public CellStyleBuilder verticalAlignment(VerticalAlignment verticalAlignment) {
            this.verticalAlignment = verticalAlignment;
            return this;
        }

        /**
         * Sets the cell border.
         *
         * @param border cell border.
         */
        public CellStyleBuilder border(CellBorder border) {
            this.border = border;
            return this;
        }

        /**
         * Sets the cell background color
         *
         * @param backgroundColor the background color.
         */
        public CellStyleBuilder backgroundColor(StyleColor backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        /**
         * Sets the same border on all sides of the cell.
         *
         * @param borderStyle the border style to be applied.
         */
        public CellStyleBuilder allSideBorder(BorderStyle borderStyle) {
            this.border = new CellBorder(borderStyle);
            return this;
        }

        /**
         * Sets a thin border on all sides of the cell.
         */
        public CellStyleBuilder thinAllSideBorder() {
            this.border = CellBorders.allSidesThin();
            return this;
        }

        /**
         * Sets the font to be applied to the cell.
         *
         * @param font the font.
         */
        public CellStyleBuilder font(StyleFont font) {
            this.font = font;
            return this;
        }

        /**
         * Sets the "Wrap Text" property on the text within the cell. In Excel, this corresponds to "Home" tab > "Alignment" section >
         * Wrap text.
         *
         * @param wrapText true if the text should be wrapped.
         */
        public CellStyleBuilder wrapText(boolean wrapText) {
            this.wrapText = wrapText;
            return this;
        }

        public WritableCellStyle build() {
            return new WritableCellStyle(this);
        }
    }
}
