
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 *
 * This file is part of the utility library and is under the MIT License.
 */

package source.utility;

import java.awt.Color;

import java.util.Random;

/**
 * {@code RandomGen} includes methods that generate random numerical, color, and
 * character values. This class extends {@link java.util.Random}, therefore as
 * stated in {@link Random},
 * <p>
 * "Instances of {@code java.util.Random} are threadsafe. However, the
 * concurrent use of the same {@code java.util.Random} instance across threads
 * may encounter contention and consequent poor performance. Consider instead
 * using {@link java.util.concurrent.ThreadLocalRandom} in multithreaded
 * designs."
 * <p>
 * Caution is advised when using instances of {@code RandomGen} in threading.
 *
 * <p>
 * <b>Planned Features:</b>
 * <p>
 * Add method longGen(long, long) which is similar to intGen(int, int) but for
 * the type {@code long}.
 *
 * <p>
 * <b>[!] Known Issues:</b>
 * <p>
 * {@link #analagousColor} sometimes attempts to create a color with invalid
 * values.
 * <p>
 * {@link #complementColor} returns a gray color if the input color is also
 * gray.
 *
 * @author Nathin Wascher
 * @version v1.3.3 - November 20, 2020
 *
 * @see Color
 * @see Random
 * @see java.util.concurrent.ThreadLocalRandom
 */
@SuppressWarnings("serial")
public final class RandomGen extends Random {

    private static final int NUM_OF_CHARACTERS = 26;
    private static final int MAX_RGB_NUM = 255;

    private char[] characters;
    private int r;
    private int g;
    private int b;

    /**
     * 
     */
    public RandomGen() {
        characters = new char[NUM_OF_CHARACTERS];
        for (int i = 0; i < NUM_OF_CHARACTERS; i++) {
            characters[i] = (char) ('a' + i);
        }
    }

    /**
     * Generates a random lower case letter.
     *
     * @return A lower case english character
     * @see #fullLetterGen()
     */
    public char letterGen() {
        return characters[intGen(0, NUM_OF_CHARACTERS - 1)];
    }

    /**
     * Generates a random lower or upper case letter.
     *
     * @return An upper or lower case english character
     * @see #intGen(int, int)
     */
    public char fullLetterGen() {
        if (boolGen()) {
            return Character.toUpperCase(letterGen());
        } else {
            return letterGen();
        }
    }

    /**
     * Generates a random RGB color.
     *
     * @return A random {@code Color}
     * @see Color
     * @see #intGen(int, int)
     */
    public Color colorGen() {
        r = intGen(0, MAX_RGB_NUM);
        g = intGen(0, MAX_RGB_NUM);
        b = intGen(0, MAX_RGB_NUM);
        return new Color(r, g, b);
    }

    /**
     * Generates a random {@code Color} within the specified values (in RGB format).
     *
     * @param rMin The minimum red {@code int} value
     * @param rMax The maximum red {@code int} value
     * @param gMin The minimum green {@code int} value
     * @param gMax The maximum green {@code int} value
     * @param bMin The minimum blue {@code int} value
     * @param bMax The maximum blue {@code int} value
     * @return A random {@code Color}
     * @see Color
     * @see #intGen(int, int)
     */
    public Color colorGen(int rMin, int rMax, int gMin, int gMax, int bMin, int bMax) {
        r = intGen(rMin, rMax);
        g = intGen(gMin, gMax);
        b = intGen(bMin, bMax);
        return new Color(r, g, b);
    }

    /**
     * Generates a color opposite of the input color.
     * <p>
     * For example: if the input color is blue, it returns the color ...
     * <p>
     * [!] WORK IN PROGRESS [!]
     *
     * @param c The input {@code Color}
     * @return A complementary {@code Color}
     * @see Color
     */
    public Color complementColor(Color c) {
        final int cMax = 180;
        final int cMin = 80;

        r = MAX_RGB_NUM - c.getRed();
        g = MAX_RGB_NUM - c.getGreen();
        b = MAX_RGB_NUM - c.getBlue();

        // temporary fix
        if (r > cMin & r < cMax) {
            if (g > cMin & g < cMax) {
                if (g > cMin & g < cMax) {
                    r = 0;
                    g = 0;
                    b = 0;
                }
            }
        }
        return new Color(r, g, b);
    }

    /**
     * Generates a color that is slightly lighter than the input color.
     * <p>
     * [!] WORK IN PROGRESS [!]
     *
     * @param c The input {@code Color}
     * @return A lighter {@code Color}
     * @see Color
     */
    public Color lighterColor(Color c) {
        r = c.getRed() * 3 / 5;
        g = c.getGreen() * 3 / 5;
        b = c.getBlue() * 3 / 5;
        return new Color(r, g, b);
    }

    /**
     * Generates a color that is slightly darker than the input color.
     * <p>
     * [!] WORK IN PROGRESS [!]
     *
     * @param c The input {@code Color}
     * @return A darker {@code Color}
     * @see Color
     */
    public Color darkerColor(Color c) {
        r = c.getRed() * 7 / 5;
        g = c.getGreen() * 7 / 5;
        b = c.getBlue() * 7 / 5;
        return new Color(r, g, b);
    }

    /**
     * Generates a color similar to the input color.
     * <p>
     * [!] WORK IN PROGRESS [!]
     *
     * @param c     The input {@code Color}
     * @param range
     * @return An analogous {@code Color}
     * @see Color
     * @see #intGen(int, int)
     */
    public Color analogousColor(Color c, int range) {
        if (boolGen()) {
            r = intGen(c.getRed() - range, c.getRed() - range / 2) % MAX_RGB_NUM;
        } else {
            r = intGen(c.getRed() + range / 2, c.getRed() + range) % MAX_RGB_NUM;
        }

        if (boolGen()) {
            g = intGen(c.getGreen() - range, c.getGreen() - range / 2) % MAX_RGB_NUM;
        } else {
            g = intGen(c.getGreen() + range / 2, c.getGreen() + range) % MAX_RGB_NUM;
        }

        if (boolGen()) {
            b = intGen(c.getBlue() - range, c.getBlue() - range / 2) % MAX_RGB_NUM;
        } else {
            b = intGen(c.getBlue() + range / 2, c.getBlue() + range) % MAX_RGB_NUM;
        }

        // temporary fix
        while (r < 0) {
            r = +MAX_RGB_NUM;
        }
        while (g < 0) {
            g = +MAX_RGB_NUM;
        }
        while (b < 0) {
            b = +MAX_RGB_NUM;
        }

        return new Color(r, g, b);
    }

    /**
     * Generates a random boolean value.
     *
     * @return A random {@code boolean}
     * @see #intGen(int, int)
     */
    public boolean boolGen() {
        if (intGen(0, 1) == 0) {
            return true;
        }
        return false;
    }

    /**
     * Generates a random byte value between min and max (inclusive).
     *
     * @param min The minimum value
     * @param max The maximum value (inclusive)
     * @return A random {@code byte} between min and max
     * @see #intGen(int, int)
     */
    public byte byteGen(byte min, byte max) {
        return (byte) (intGen(min, max));
    }

    /**
     * Generates a random short value between min and max (inclusive).
     *
     * @param min The minimum value
     * @param max The maximum value (inclusive)
     * @return A random {@code short} between min and max
     * @see #intGen(int, int)
     */
    public short shortGen(short min, short max) {
        return (short) (intGen(min, max));
    }

    /**
     * Generates a random int value between min and max (inclusive).
     *
     * @param min The minimum value
     * @param max The maximum value (inclusive)
     * @return A random {@code int} between min and max
     * @see Random#nextInt(int)
     */
    public int intGen(int min, int max) {
        return nextInt(max - min + 1) + min;
    }
}
