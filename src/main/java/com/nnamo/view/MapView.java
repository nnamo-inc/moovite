package com.nnamo.view;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;

public class MapView extends JFrame {

    MapPanel mapPanel = new MapPanel();
    GUIPanel guiPanel = new GUIPanel();
    JLayeredPane layeredPane = new JLayeredPane();

    // COSTRUCTOR //
    public MapView() throws IOException {
        super("Moovite Map View");
        setSize(getScreenSize());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mapPanel.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(mapPanel, JLayeredPane.DEFAULT_LAYER);

        guiPanel.setBounds(0, 0, getWidth(), getHeight());
        guiPanel.setOpaque(false);
        layeredPane.add(guiPanel, JLayeredPane.PALETTE_LAYER);

        setContentPane(layeredPane);
        setVisible(true);
    }

    // METHODS //
    private Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    private int getScreenWidth() {
        return (int) getScreenSize().getWidth();
    }

    private int getScreenHeight() {
        return (int) getScreenSize().getHeight();
    }
    // GETTERS AND SETTERS //
    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public GUIPanel getGUIPanel() {
        return guiPanel;
    }
}
