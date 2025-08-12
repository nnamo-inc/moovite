package com.nnamo.view.customcomponents;

import com.google.transit.realtime.GtfsRealtime;
import com.nnamo.interfaces.StatisticInterface;
import com.nnamo.services.FeedUpdateListener;
import com.nnamo.utils.CustomColor;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public abstract class StatisticUnit extends JLabel implements StatisticInterface, FeedUpdateListener {
    private String name;
    private String value;
    private String unit;
    private final int CORNER_RADIUS = 15;

    public StatisticUnit(String name, String unit, Color color) {
        super();
        this.name = name;
        this.unit = unit;

        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setFont(new Font("Segoe UI", Font.BOLD, 16));
        this.setText("<html><div style='text-align: center;'>" + name + "<br>" + value + " <span style='font-size: 12px;'>" + unit + "</span></div></html>");
        this.setOpaque(false); // Important: set to false so we can draw custom background
        this.setBackground(color);
        this.setForeground(getAdaptiveTextColor(color));
    }

    /**
     * Calculate appropriate text color based on background brightness
     * Uses relative luminance formula for better accessibility
     */
    private Color getAdaptiveTextColor(Color backgroundColor) {
        // Calculate relative luminance using sRGB formula
        double red = backgroundColor.getRed() / 255.0;
        double green = backgroundColor.getGreen() / 255.0;
        double blue = backgroundColor.getBlue() / 255.0;

        // Apply gamma correction
        red = red <= 0.03928 ? red / 12.92 : Math.pow((red + 0.055) / 1.055, 2.4);
        green = green <= 0.03928 ? green / 12.92 : Math.pow((green + 0.055) / 1.055, 2.4);
        blue = blue <= 0.03928 ? blue / 12.92 : Math.pow((blue + 0.055) / 1.055, 2.4);

        double luminance = 0.2126 * red + 0.7152 * green + 0.0722 * blue;

        // Use white text for dark backgrounds, dark text for light backgrounds
        return luminance > 0.5 ? new Color(33, 33, 33) : Color.WHITE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Enable antialiasing for smooth rounded corners
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded rectangle background
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

        g2d.dispose();

        // Call super to draw the text
        super.paintComponent(g);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
        this.setText("<html><div style='text-align: center; font-weight: bold;'>" + name + "<br><span style='font-size: 18px; font-weight: 900;'>" + value + "</span> <span style='font-size: 12px; font-weight: 600;'>" + unit + "</span></div></html>");
    }

    public abstract void onFeedUpdated(List<GtfsRealtime.FeedEntity> entities);
}
