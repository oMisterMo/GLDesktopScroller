package com.ds.mo.common;

import java.util.Random;

/**
 * Add ability to use own sin function
 * <p/>
 * 26-JAN-2017, 01:50:14.
 *
 * @author Mo
 */
public final class Helper {

    private static Random random = new Random();

    private Helper() {
    }

    public static int Clamp(int val, int min, int max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        }
        //We have a value within the range
        return val;
    }

    public static float Clamp(float val, float min, float max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        }
        return val;
    }

    /**
     * Gets the sign of the number given
     *
     * @param x float to evaluate
     * @return Returns 1 or -1 depending on state of input
     */
    public static int Sign(float x) {
        if (x >= 0) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Returns a random value between 0 and 1
     *
     * @return 0 -> 1
     */
    public static float Random() {
        return random.nextFloat();
    }

    /**
     * Random number between min and max (inclusive)
     *
     * @param min
     * @param max
     * @return
     */
    public static int Random(int min, int max) {
        return (int) Math.floor(random.nextFloat() * (max - min + 1) + min);
//        return (int) Math.floor(random.nextFloat() * (max - min) + min);
    }

    /**
     * .floor() Returns the largest (closest to positive infinity) double value
     * that is less than or equal to the argument and is equal to a mathematical
     * integer. Special cases:
     *
     * 1. If the argument value is already equal to a mathematical integer, then
     * the result is the same as the argument.
     *
     * 2. If the argument is NaN or an infinity or positive zero or negative
     * zero, then the result is the same as the argument.
     *
     * @param min
     * @param max
     * @return
     */

    /**
     * No error checks (number, range)
     *
     * @param min
     * @param max
     * @return
     */
    public static float Random(float min, float max) {
        return (random.nextFloat() * (max - min)) + min;
    }

    //-----------------Extras-----------------
    public static float[] LerpColor(float[] colA, float[] colB, float amount) {
        //float[0] = r
        //float[1] = g
        //float[2] = b
        //float[3] = a
        float[] color;
//        colA[0] * (1 - amount) + colB[0] * amount;
        return null;
    }

    public static float Lerp(float a, float b, float t) {
        return a * (1 - t) + (b * t);
    }

    public static int floatToIntBits(float value) {
        return Float.floatToIntBits(value);
    }

    public static int floatToRawIntBits(float value) {
        return Float.floatToRawIntBits(value);
    }

    public static int floatToIntColor(float value) {
        return Float.floatToRawIntBits(value);
    }

    /**
     * Encodes the ABGR int color as a float. The high bits are masked to avoid using floats in the NaN range, which unfortunately
     * means the full range of alpha cannot be used. See {@link Float#intBitsToFloat(int)} javadocs.
     */
    public static float intToFloatColor(int value) {
        return Float.intBitsToFloat(value & 0xfeffffff);
    }

    public static float intBitsToFloat(int value) {
        return Float.intBitsToFloat(value);
    }
}
