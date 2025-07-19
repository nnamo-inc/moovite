package com.nnamo.view;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;

public class MapView extends JFrame {

    MapPanel mapPanel = new MapPanel();
    StopPanel stopPanel = new StopPanel();

    // COSTRUCTOR //
    public MapView() throws IOException {
        super("Moovite Map View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        getContentPane().add(mapPanel, BorderLayout.CENTER);
        getContentPane().add(stopPanel, BorderLayout.PAGE_END);
        setVisible(true);
    }

    // GETTERS AND SETTERS //
    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public StopPanel getStopPanel() {
        return stopPanel;
    }
}
