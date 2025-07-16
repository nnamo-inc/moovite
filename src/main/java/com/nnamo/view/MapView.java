package com.nnamo.view;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;

public class MapView extends JFrame {

    MapPanel mapPanel = new MapPanel();

    public MapView() throws IOException {
        super("Moovite Map View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        getContentPane().add(mapPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }
}
