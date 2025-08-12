package com.nnamo.utils;

import java.awt.*;

public class CustomColor {
    public static final Color RED = new Color(105, 16, 0);
    public static final Color GREEN = new Color(37, 105, 0);

    public static Color getRandomColor() {
        float hue = (float) Math.random(); // [0, 1]
        float saturation = 0.5f + (float) Math.random() * 0.5f; // [0.5, 1.0] => avoid gray
        float brightness = 0.7f + (float) Math.random() * 0.3f; // [0.7, 1.0] => avoid dark
        return Color.getHSBColor(hue, saturation, brightness);
    }
}