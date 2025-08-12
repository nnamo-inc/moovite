package com.nnamo.view.components;

import com.google.transit.realtime.GtfsRealtime;
import com.nnamo.services.FeedUpdateListener;
import com.nnamo.view.customcomponents.CustomGbc;
import com.nnamo.view.customcomponents.StatisticTotalBus;
import com.nnamo.view.customcomponents.StatisticUnit;
import com.nnamo.view.customcomponents.WrapLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class StatisticsPanel extends JPanel {
    private final JPanel tileContainer = new JPanel();

    public StatisticsPanel() {
        super();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel titleLabel = new JLabel("Statistics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, new CustomGbc().setPosition(0, 0)
                .setAnchor(GridBagConstraints.NORTH)
                .setInsets(5, 5, 5, 5));

        // tile container with custom wrap layout
        tileContainer.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
        tileContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(tileContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, new CustomGbc().setPosition(0, 1)
                .setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0)
                .setInsets(5, 5, 5, 5));

        setVisible(false);
    }

    public void addStatisticTile(StatisticUnit tile) {
        if (tile == null) {
            throw new IllegalArgumentException("Tile cannot be null");
        }

        // Set preferred and minimum sizes to maintain consistent tile dimensions
        Dimension tileSize = new Dimension(120, 80); // Increased size for better visibility
        tile.setPreferredSize(tileSize);
        tile.setMinimumSize(tileSize);
        tile.setMaximumSize(tileSize);

        tileContainer.add(tile);
        tileContainer.revalidate();
        tileContainer.repaint();
    }
}
