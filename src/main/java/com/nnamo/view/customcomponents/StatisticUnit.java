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

    public StatisticUnit(String name, String unit) {
        super();
        this.name = name;
        this.unit = unit;

        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setFont(new Font("Arial", Font.BOLD, 16));
        this.setText("<html><div style='text-align: center;'>" + name + "<br>" + value + " <span style='font-size: 12px;'>" + unit + "</span></div></html>");
        this.setOpaque(false); // Important: set to false so we can draw custom background
        this.setBackground(CustomColor.getRandomColor());
        this.setForeground(Color.BLACK);
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
    }

    public abstract void onFeedUpdated(List<GtfsRealtime.FeedEntity> entities);
}
