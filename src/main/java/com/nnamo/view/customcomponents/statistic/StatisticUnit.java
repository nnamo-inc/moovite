package com.nnamo.view.customcomponents.statistic;

import com.google.transit.realtime.GtfsRealtime;
import com.nnamo.enums.RealtimeMetricType;
import com.nnamo.interfaces.StatisticInterface;
import com.nnamo.services.FeedUpdateListener;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Abstract class representing a statistic unit tile in the UI.
 * Extends {@link JLabel} to display the statistic name, value, and unit.
 * Implements {@link StatisticInterface} for getting/setting values and units,
 * and {@link FeedUpdateListener} to update statistics based on feed data.
 *
 * @author Davide Galilei
 * @see JLabel
 * @see StatisticInterface
 * @see FeedUpdateListener
 */
public abstract class StatisticUnit extends JLabel implements StatisticInterface, FeedUpdateListener {
    private final String name;
    private String value;
    private final String unit;
    private final int CORNER_RADIUS = 15;

    private final List<StatisticUpdateListener> listeners = new java.util.ArrayList<>();
    private MetricCollector metricCollector;

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
     *
     * @param backgroundColor the background color of the tile
     * @author AI
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

    public abstract RealtimeMetricType getMetricType();

    public abstract int computeMetric(List<GtfsRealtime.FeedEntity> entities);

    public final void onFeedUpdated(List<GtfsRealtime.FeedEntity> entities) {
        int metric = computeMetric(entities);
        setValue(String.valueOf(metric));
        repaint(); // Refresh the display
        notifyStatisticUpdateListeners(metric);

        // Update the database with the new metric value
        if (metricCollector != null) {
            metricCollector.onProducedMetric(getMetricType(), metric);
        }
    }

    public void addStatisticUpdateListener(StatisticUpdateListener listener) {
        listeners.add(listener);
    }

    public void notifyStatisticUpdateListeners(int value) {
        for (StatisticUpdateListener listener : listeners) {
            listener.onStatisticUpdated(this.getMetricType(), value);
        }
    }

    public void setMetricCollector(MetricCollector collector) {
        this.metricCollector = collector;
    }
}
