package com.nnamo.view.frame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.swing.*;

import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.nnamo.enums.RealtimeStatus;
import com.nnamo.interfaces.FavoriteBehaviour;
import com.nnamo.interfaces.SwitchBarListener;
import com.nnamo.interfaces.TableRowClickListener;
import com.nnamo.models.StopModel;
import com.nnamo.models.StopTimeModel;
import com.nnamo.models.RealtimeStopUpdate;
import com.nnamo.view.components.LeftPanel;
import com.nnamo.view.components.MapPanel;
import com.nnamo.view.components.SearchPanel;
import com.nnamo.view.components.StopPanel;
import org.jxmapviewer.viewer.GeoPosition;

public class MainFrame extends JFrame {
    MapPanel mapPanel = new MapPanel();
    StopPanel stopPanel = new StopPanel();
    LeftPanel leftPanel = new LeftPanel();

    JSplitPane splitMapStop;
    JSplitPane splitSearchCenter;

    public MainFrame() throws IOException {
        super("Moovite Map View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(new Dimension(1000, 800));
        setLayout(new BorderLayout());

        // set resources/icons/application-bar-icon.png as the app icon
        try {
            setIconImage(
                    Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/application-bar-icon.png")));
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Icon not found, using default icon.");
        }

        // Split verticale tra mappa e stop panel
        splitMapStop = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mapPanel, stopPanel);
        splitMapStop.setResizeWeight(1.0);

        // Split orizzontale tra search panel e centro (mappa+stop)
        splitSearchCenter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, splitMapStop);
        splitSearchCenter.setResizeWeight(0.2);

        add(splitSearchCenter, BorderLayout.CENTER);
    }

    public void open() {
        setVisible(true);
    }

    public void close() {
        setVisible(false);
    }

    public void renderStops(List<StopModel> stops) {
        mapPanel.renderStops(stops);
    }

    public void renderVehiclePositions(List<VehiclePosition> positions) {
        mapPanel.renderVehiclePositions(positions);
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

    public void updateStopPanelVisibility(boolean visible) {
        JSplitPane jsp = splitMapStop;
        if (visible) {
            this.stopPanel.setVisible(visible);
            jsp.setDividerSize(10);
            jsp.setDividerLocation(0.6);
            getStopPanel().revalidate();
        } else {
            this.stopPanel.setVisible(visible);
            jsp.setDividerSize(0);
            jsp.setDividerLocation(1.0);
            getStopPanel().revalidate();
        }

    }

    // GETTERS AND SETTERS //
    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public StopPanel getStopPanel() {
        return stopPanel;
    }

    public SearchPanel getSearchPanel() {
        return leftPanel.getSearchPanel();
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
        leftPanel.setSearchStopTableClickListener(listener);
    }

    public void setSearchRouteTableClickListener(TableRowClickListener listener) {
        leftPanel.setSearchRouteTableClickListener(listener);
    }

    public void updateStopPanelPreferButtons(boolean favorite, String stop) {
        this.stopPanel.updatePreferButtons(favorite);
    }

    public void setMapPanelMapPosition(GeoPosition geoPosition, int zoomLevel) {
        this.mapPanel.setMapPanelMapPosition(geoPosition, zoomLevel);
    }

    public void setLocalMapCache(File cacheDir) {
        mapPanel.setLocalMapCache(cacheDir);
    }

    public void setRealtimeSwitchListener(SwitchBarListener listener) {
        this.leftPanel.setRealtimeSwitchListener(listener);
    }

    public void setRealtimeStatus(RealtimeStatus status) {
        this.leftPanel.setRealtimeStatus(status);
    }
}
