package com.sondertara.excel.meta.style.border;

import com.sondertara.excel.meta.style.color.StyleColor;
import org.apache.poi.ss.usermodel.BorderStyle;

import java.util.Objects;

/**
 * Defines the style of the border surrounding one or multiple cells.
 */
public class CellBorder {

    private final BorderStyle topBorderStyle;
    private final BorderStyle rightBorderStyle;
    private final BorderStyle bottomBorderStyle;
    private final BorderStyle leftBorderStyle;

    private final StyleColor color;

    /**
     * Constructs a border which is the same on all sides and has the default border color.
     *
     * @param borderStyle the border style to be used, i.e. thin, thick or other.
     */
    public CellBorder(BorderStyle borderStyle) {
        topBorderStyle = borderStyle;
        rightBorderStyle = borderStyle;
        bottomBorderStyle = borderStyle;
        leftBorderStyle = borderStyle;
        color = null;
    }

    private CellBorder(CellBorderBuilder builder) {
        topBorderStyle = builder.topBorderStyle;
        rightBorderStyle = builder.rightBorderStyle;
        bottomBorderStyle = builder.bottomBorderStyle;
        leftBorderStyle = builder.leftBorderStyle;
        color = builder.color;
    }

    public BorderStyle getTopBorderStyle() {
        return topBorderStyle;
    }

    public BorderStyle getRightBorderStyle() {
        return rightBorderStyle;
    }

    public BorderStyle getBottomBorderStyle() {
        return bottomBorderStyle;
    }

    public BorderStyle getLeftBorderStyle() {
        return leftBorderStyle;
    }

    public StyleColor getColor() {
        return color;
    }

    /**
     * Combines the properties of this border with the properties of another one into a new border.
     * Defined properties have priority over non-defined properties.
     * The properties of this border have priority over the other border.
     *
     * @param other another border.
     * @return a new border, with properties combined.
     */
    public CellBorder combineWith(CellBorder other) {
        return new CellBorderBuilder()
                .topBorderStyle(topBorderStyle != null ? topBorderStyle : other.topBorderStyle)
                .rightBorderStyle(rightBorderStyle != null ? rightBorderStyle : other.rightBorderStyle)
                .bottomBorderStyle(bottomBorderStyle != null ? bottomBorderStyle : other.bottomBorderStyle)
                .leftBorderStyle(leftBorderStyle != null ? leftBorderStyle : other.leftBorderStyle)
                .color(color != null ? color : other.color)
                .build();
    }

    public static CellBorderBuilder builder() {
        return new CellBorderBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CellBorder that = (CellBorder) o;
        return topBorderStyle == that.topBorderStyle
                && rightBorderStyle == that.rightBorderStyle
                && bottomBorderStyle == that.bottomBorderStyle
                && leftBorderStyle == that.leftBorderStyle
                && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topBorderStyle, rightBorderStyle, bottomBorderStyle, leftBorderStyle, color);
    }

    public static final class CellBorderBuilder {

        private BorderStyle topBorderStyle;
        private BorderStyle rightBorderStyle;
        private BorderStyle bottomBorderStyle;
        private BorderStyle leftBorderStyle;

        private StyleColor color;

        /**
         * Specifies the style of the top side of the border.
         *
         * @param topBorderStyle style of the border.
         */
        public CellBorderBuilder topBorderStyle(BorderStyle topBorderStyle) {
            this.topBorderStyle = topBorderStyle;
            return this;
        }

        /**
         * Specifies the style of the right side of the border.
         *
         * @param rightBorderStyle style of the border.
         */
        public CellBorderBuilder rightBorderStyle(BorderStyle rightBorderStyle) {
            this.rightBorderStyle = rightBorderStyle;
            return this;
        }

        /**
         * Specifies the style of the bottom side of the border.
         *
         * @param bottomBorderStyle style of the border.
         */
        public CellBorderBuilder bottomBorderStyle(BorderStyle bottomBorderStyle) {
            this.bottomBorderStyle = bottomBorderStyle;
            return this;
        }

        /**
         * Specifies the style of the left side of the border.
         *
         * @param leftBorderStyle style of the border.
         */
        public CellBorderBuilder leftBorderStyle(BorderStyle leftBorderStyle) {
            this.leftBorderStyle = leftBorderStyle;
            return this;
        }

        /**
         * Specifies the color of the border on all sides.
         *
         * @param color color of the border.
         */
        public CellBorderBuilder color(StyleColor color) {
            this.color = color;
            return this;
        }

        public CellBorder build() {
            return new CellBorder(this);
        }
    }
}
