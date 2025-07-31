package com.nnamo.view.frame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.swing.*;

import com.nnamo.interfaces.FavoriteBehaviour;
import com.nnamo.interfaces.TableRowClickListener;
import com.nnamo.models.StopTimeModel;
import com.nnamo.services.RealtimeStopUpdate;
import com.nnamo.view.components.MapPanel;
import com.nnamo.view.components.SearchPanel;
import com.nnamo.view.components.StopPanel;
import org.jxmapviewer.viewer.GeoPosition;

public class MainFrame extends JFrame {
    MapPanel mapPanel = new MapPanel();
    StopPanel stopPanel = new StopPanel();
    SearchPanel searchPanel = new SearchPanel();

    // Center panel contains map, stop, search
    JPanel centerPanel = new JPanel();

    // COSTRUCTOR //
    public MainFrame() throws IOException {
        super("Moovite Map View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        this.stopPanel.updateStopPanelInfo(id, nome);
    }

    public void updateStopPanelTimes(List<StopTimeModel> stopTimes, List<RealtimeStopUpdate> realtimeUpdates) {
        this.stopPanel.updateStopTimes(stopTimes, realtimeUpdates);
    }

    public void updatePreferRouteButton(Boolean isFavorite, String routeNumber) {
        this.stopPanel.updatePreferRouteButton(isFavorite, routeNumber);
    }

    // GETTERS AND SETTERS //
    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public StopPanel getStopPanel() {
        return stopPanel;
    }

    public SearchPanel getSearchPanel() {
        return searchPanel;
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

    // SETTERS FOR BEHAVIOURS //

    public void setFavStopBehaviour(FavoriteBehaviour behaviour) {
        this.stopPanel.setFavStopBehaviour(behaviour);
    }

    public void setFavLineBehaviour(FavoriteBehaviour behaviour) {
        this.stopPanel.setFavRouteBehaviour(behaviour);
    }

    public void setStopTimeTableClickListener(TableRowClickListener listener) {
        this.stopPanel.setTableClickListener(listener);
    }

    public void setSearchStopTableClickListener(TableRowClickListener listener) {
        this.searchPanel.setSearchStopTableClickListener(listener);
    }

    public void setSearchRouteTableClickListener(TableRowClickListener listener) {
        this.searchPanel.setSearchRouteTableClickListener(listener);
    }

    public void updateStopPanelPreferButtons(boolean favorite, String stop) {
        this.stopPanel.updatePreferButtons(favorite);
    }

    public void setMapPanelMapPosition(GeoPosition geoPosition, int zoomLevel) {
        this.mapPanel.setMapPanelMapPosition(geoPosition, zoomLevel);
    }
}
