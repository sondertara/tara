package com.sondertara.excel.meta.style.color;

import java.util.Objects;

/**
 * Defines the color of some style.
 * For .xls files, a similar color to the one defined will be used, since the color palette is limited in size.
 */
public class StyleColor {

    public static final StyleColor BLACK = new StyleColor(0, 0, 0);

    private final byte red;
    private final byte green;
    private final byte blue;

    /**
     * Creates a color based on rgb values. Each value should be between 0 and 255, both inclusive.
     */
    public StyleColor(int red, int green, int blue) {
        validateColorValueRange(red, green, blue);
        this.red = (byte) red;
        this.green = (byte) green;
        this.blue = (byte) blue;
    }

    public byte getRed() {
        return red;
    }

    public byte getGreen() {
        return green;
    }

    public byte getBlue() {
        return blue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StyleColor cellColor = (StyleColor) o;
        return red == cellColor.red && green == cellColor.green && blue == cellColor.blue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue);
    }

    @Override
    public String toString() {
        return "CellColor{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                '}';
    }

    private static void validateColorValueRange(int r, int g, int b) {
        boolean rangeError = false;
        String badComponentString = "";

        if (r < 0 || r > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Red";
        }
        if (g < 0 || g > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Green";
        }
        if (b < 0 || b > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Blue";
        }
        if (rangeError) {
            throw new IllegalArgumentException("Color parameter outside of expected range:" + badComponentString);
        }
    }
}
