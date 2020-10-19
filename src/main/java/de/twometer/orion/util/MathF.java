package de.twometer.orion.util;

public class MathF {

    public static final float PI = (float) Math.PI;

    public static final float E = (float) Math.E;

    public static float sin(float a) {
        return (float) Math.sin(a);
    }

    public static float asin(float a) {
        return (float) Math.asin(a);
    }

    public static float cos(float a) {
        return (float) Math.cos(a);
    }

    public static float acos(float a) {
        return (float) Math.acos(a);
    }

    public static float tan(float a) {
        return (float) Math.tan(a);
    }

    public static float atan(float a) {
        return (float) Math.tan(a);
    }

    public static float atan2(float x, float y) {
        return (float) Math.atan2(y, x);
    }

    public static float toRadians(float deg) {
        return (float) Math.toRadians(deg);
    }

    public static float toDegrees(float rad) {
        return (float) Math.toDegrees(rad);
    }

    public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

}
