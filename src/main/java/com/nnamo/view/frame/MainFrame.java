package com.nnamo.view.frame;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import javax.swing.*;

import com.nnamo.models.StopTimeModel;
import com.nnamo.view.components.MapPanel;
import com.nnamo.view.components.SearchPanel;
import com.nnamo.view.components.StopPanel;

public class MainFrame extends JFrame {
    MapPanel mapPanel = new MapPanel();
    StopPanel stopPanel = new StopPanel();
    SearchPanel searchPanel = new SearchPanel();

    JPanel centerPanel = new JPanel();

    // COSTRUCTOR //
    public MainFrame() throws IOException {
        // Set the JFrame properties
        super("Moovite Map View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        // Initialize the center panel with the map and stop panels then add it to the
        // JFrame
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(mapPanel, BorderLayout.CENTER);
        centerPanel.add(stopPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        // Add the search panel to the JFrame
        add(searchPanel, BorderLayout.WEST);
        setVisible(true);
    }

    // METHODS //
    private Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    // GETTERS AND SETTERS //
    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public StopPanel getStopPanel() {
        return stopPanel;
    }

    public void updateStopTimes(List<StopTimeModel> stopTimes) {
        this.stopPanel.updateStopTimes(stopTimes);
    }
}
