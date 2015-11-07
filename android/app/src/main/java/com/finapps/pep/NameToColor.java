package com.finapps.pep;

/**
 * Class to get a color from a name.
 */
public final class NameToColor {
    private NameToColor() {
        throw new AssertionError();
    }

    private static final int colors[] = {
            0xCAFAF700,
            0xE5FACA00,
            0xDFCAFA00,
            0xFACACA00,
            0xF8FACA00,
    };

    public static int getColor(String name) {
        return colors[name.hashCode()%colors.length];
    }
}
