package com.sondertara.excel.meta.style.font;

import com.sondertara.excel.meta.style.color.StyleColor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Defines the font properties.
 * Not providing some property means that the Excel default will be used.
 */
public class StyleFont {

    private final String name;
    private final short size;
    private final StyleColor color;

    private final boolean bold;
    private final boolean italic;
    private final boolean underLine;

    private StyleFont(StyleFontBuilder styleFontBuilder) {
        name = styleFontBuilder.name;
        size = styleFontBuilder.size;
        color = styleFontBuilder.color;
        bold = styleFontBuilder.bold;
        italic = styleFontBuilder.italic;
        underLine = styleFontBuilder.underLine;
    }

    public String getName() {
        return name;
    }

    public short getSize() {
        return size;
    }

    public StyleColor getColor() {
        return color;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public boolean isUnderLine() {
        return underLine;
    }

    /**
     * Combines the properties of this font with the properties of another one into a new font.
     * Defined properties have priority over non-defined properties.
     * The properties of this font have priority over the other font, except boolean properties, where "true" has priority.
     *
     * @param other another font.
     * @return a new font, with properties combined.
     */
    public StyleFont combineWith(StyleFont other) {
        return new StyleFontBuilder()
                .name(!StringUtils.isEmpty(name) ? name : other.name)
                .size(size > 0 ? size : other.size)
                .color(color != null ? color : other.color)
                .bold(bold || other.bold)
                .italic(italic || other.italic)
                .underLine(underLine || other.underLine)
                .build();
    }

    public static StyleFontBuilder builder() {
        return new StyleFontBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StyleFont styleFont = (StyleFont) o;
        return Objects.equals(name, styleFont.name)
                && size == styleFont.size
                && bold == styleFont.bold
                && Objects.equals(color, styleFont.color)
                && italic == styleFont.italic
                && underLine == styleFont.underLine;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size, bold, italic, underLine);
    }

    @Override
    public String toString() {
        return "CellFont{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", color=" + color +
                ", bold=" + bold +
                ", italic=" + italic +
                ", underLine=" + underLine +
                '}';
    }

    public static final class StyleFontBuilder {

        private String name;
        private short size;
        private StyleColor color;

        private boolean bold;
        private boolean italic;
        private boolean underLine;

        /**
         * Specifies the name of the font. If not specified, the default Excel font is used.
         *
         * @param name the name of the font.
         */
        public StyleFontBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Specifies the size of the font. If not specified, the default Excel font size is used.
         *
         * @param size the size of the font.
         */
        public StyleFontBuilder size(int size) {
            this.size = (short) size;
            return this;
        }

        /**
         * Specifies the color of the font. If not specified, the default Excel font color (black) is used.
         *
         * @param color the size of the font.
         */
        public StyleFontBuilder color(StyleColor color) {
            this.color = color;
            return this;
        }

        /**
         * Specifies the bold property of the font. False by default.
         *
         * @param bold true if the font should be bold.
         */
        public StyleFontBuilder bold(boolean bold) {
            this.bold = bold;
            return this;
        }

        /**
         * Specifies the italic property of the font. False by default.
         *
         * @param italic true if the font should be italic.
         */
        public StyleFontBuilder italic(boolean italic) {
            this.italic = italic;
            return this;
        }

        /**
         * Specifies the underline property of the font. None by default.
         *
         * @param underLine true if the font text should be underlined.
         */
        public StyleFontBuilder underLine(boolean underLine) {
            this.underLine = underLine;
            return this;
        }

        public StyleFont build() {
            return new StyleFont(this);
        }
    }
}
