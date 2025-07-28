package com.nnamo.view.frame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.swing.*;

import com.nnamo.interfaces.FavoriteLineBehaviour;
import com.nnamo.interfaces.FavoriteStopBehaviour;
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
        super("Moovite Map View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /* setExtendedState(JFrame.MAXIMIZED_BOTH); */
        setSize(new Dimension(1000, 800));
        setLayout(new BorderLayout());
        // Initialize the center panel with the map and stop panels then add it to the
        // JFrame
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(mapPanel, BorderLayout.CENTER);
        centerPanel.add(stopPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        // Add the search panel to the JFrame
        add(searchPanel, BorderLayout.WEST);
    }

    public void open() {
        setVisible(true);
    }

    public void close() {
        setVisible(false);
    }

    // METHODS //
    private Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public void updateStopPanelInfo(String id, String nome) {
        this.stopPanel.getTextID().setText(id);
        this.stopPanel.getTextName().setText(nome);
    }

    public void updateStopPanelTimes(List<StopTimeModel> stopTimes) {
        this.stopPanel.updateStopTimes(stopTimes);
    }

    public void updateStopPanelPreferStopButton(String string) {
        this.stopPanel.updateFavoriteStopMessage(string);
    }

    public void updateStopPanelPreferRouteButton(String string) {
        this.stopPanel.updateFavoriteRouteMessage(string);
    }

    public boolean isRouteButtonEnabled() {
        return this.stopPanel.isRouteButtonEnabled();
    }

    // GETTERS AND SETTERS //
    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public StopPanel getStopPanel() {
        return stopPanel;
    }

    public BufferedImage getCurrentStopIcon() {
        return this.getMapPanel().getStopPainter().getCurrentIcon();
    }

    public void setStopId(String id) {
        if (id != null) {
            this.getStopPanel().getTextID().setText(id);
        }
    }

    public void setStopName(String name) {
        if (name != null) {
            this.getStopPanel().getTextName().setText(name);
        }
    }

    public void setFavStopBehaviour(FavoriteStopBehaviour behaviour) {
        this.stopPanel.setFavStopBehaviour(behaviour);
    }

    public void setFavLineBehaviour(FavoriteLineBehaviour behaviour) {
        this.stopPanel.setFavLineBehaviour(behaviour);
    }

    public void setFavoriteStopFlag(boolean favorite) {
        this.stopPanel.setFavoriteStopFlag(favorite);
    }

}
