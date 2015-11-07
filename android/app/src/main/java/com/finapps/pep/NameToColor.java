package com.finapps.pep;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to get a color from a name.
 */
public final class NameToColor {
    private NameToColor() {
        throw new AssertionError();
    }

    private static Map<String, Integer> map = new HashMap<>();

    private static final int colors[] = {
            0xCAFAF700,
            0xE5FACA00,
            0xDFCAFA00,
            0xFACACA00,
            0xF8FACA00,
            0x96FFF800,
            0xFFBAFE00,
            0xBAB3FF00,
            0xFFB3B300,
            0xE1B5FF00,
            0xB5F8FF00,
            0xC4E87600,
            0xCFCFCF00,
    };

    public static int getColor(String name) {
        if (!map.containsKey(name)) {
            map.put(name, map.size());
        }
        return colors[map.get(name)];
    }
}
