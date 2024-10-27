package com.sondertara.excel.meta.style.font;

/**
 * Provides some default font styles that can be used.
 *
 * @see StyleFont
 */
public class StyleFonts {

    private static final StyleFont BOLD = StyleFont.builder()
            .bold(true)
            .build();

    private static final StyleFont ITALIC = StyleFont.builder()
            .italic(true)
            .build();

    private StyleFonts() {
        throw new IllegalStateException("Utility class");
    }

    public static StyleFont bold() {
        return BOLD;
    }

    public static StyleFont italic() {
        return ITALIC;
    }
}
