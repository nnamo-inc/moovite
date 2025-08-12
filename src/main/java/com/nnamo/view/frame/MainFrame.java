package com.nnamo.view.frame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.*;

import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.nnamo.enums.RealtimeStatus;
import com.nnamo.enums.UpdateMode;
import com.nnamo.interfaces.*;
import com.nnamo.models.RouteModel;
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
    JSplitPane splitLeftMap;

    public MainFrame() throws IOException {
        super("Moovite Map View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /* setExtendedState(JFrame.MAXIMIZED_BOTH); */
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
        splitMapStop.setDividerSize(0);

        // Split orizzontale tra search panel e centro (mappa+stop)
        splitLeftMap = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, splitMapStop);
        splitLeftMap.setResizeWeight(0.0);
        splitLeftMap.setDividerSize(0);

        add(splitLeftMap, BorderLayout.CENTER);
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

    public void renderRouteLines(List<StopModel> stopModels, List<VehiclePosition> routePositions, String routeId, GeoPosition geoPosition, int zoomLevel) {
        mapPanel.renderStopsRoute(stopModels);
        mapPanel.renderVehiclePositions(routePositions);
        mapPanel.repaintView();
        setCurrentRouteId(routeId);
        if (geoPosition != null) {
            setMapPanelMapPosition(geoPosition, zoomLevel);
        }
    }

    public void renderVehiclePositions(List<VehiclePosition> positions) {
        mapPanel.renderVehiclePositions(positions);
    }

    // METHODS //
    private Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public void updateStopPanelInfo(String id, String nome) {
        this.stopPanel.updateStopInfo(id, nome);
    }

    public void updateStopPanelTimes(List<StopTimeModel> stopTimes, List<RealtimeStopUpdate> realtimeUpdates) {
        this.stopPanel.updateStopTimes(stopTimes, realtimeUpdates);
    }

    public void updateStopPanelRoutes(List<StopTimeModel> stopTimes) {
        this.stopPanel.updateStopRoutes(stopTimes);
    }

    public void updatePreferRouteButton(Boolean isFavorite, String routeNumber) {
        this.stopPanel.updatePreferRouteButton(isFavorite, routeNumber);
        this.leftPanel.updatePreferRouteButton(isFavorite, routeNumber);
    }

    public void updatePreferStopButton(Boolean isFavorite, String routeId) {
        this.leftPanel.updatePreferStopButton(isFavorite, routeId);
        this.stopPanel.updatePreferStopButton(isFavorite, routeId);
    };

    public void updateStopPanelVisibility(boolean visible) {
        JSplitPane jsp = splitMapStop;
        if (visible) {
            stopPanel.open();
            jsp.setDividerSize(10);
            jsp.setDividerLocation(0.6);
        } else {
            stopPanel.close();
            jsp.setDividerSize(0);
            jsp.setDividerLocation(1.0);
        }
        stopPanel.revalidate();
    }

    public void updateLeftPanelVisibility(boolean visible) {
        JSplitPane jsp = splitLeftMap;
        if (visible) {
            jsp.setDividerSize(10);
            jsp.setDividerLocation(0.3);
        } else {
            int buttonPanelWidth = leftPanel.getButtonPanel().getPreferredSize().width;
            jsp.setDividerSize(0);
            jsp.setDividerLocation(buttonPanelWidth);
        }
        leftPanel.revalidate();
    }

    public void updateLeftPanelModularPanel(JPanel panel, boolean isVisible) {
        leftPanel.updateModularPanel(panel, isVisible);
    }

    public void initLeftPanelPreferPanelPreferTable(List<StopModel> stops, List<RouteModel> routes) {
        leftPanel.initPreferPanelPreferTable(stops, routes);
    }

    public void updateFavStopTable(StopModel stop, UpdateMode updateMode) {
        leftPanel.updateFavStopTable(stop, updateMode);
    }

    public void updateFavRouteTable(RouteModel route, UpdateMode updateMode) {
        leftPanel.updateFavRouteTable(route, updateMode);
    }

    // GETTERS AND SETTERS //
    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public String getCurrentStopId() {
        return stopPanel.getStopId();
    }

    public String getCurrentRouteId() {
        return mapPanel.getCurrentRouteId();
    }

    public void setCurrentRouteId(String routeId) {
        mapPanel.setCurrentRouteId(routeId);
    }

    public StopPanel getStopPanel() {
        return stopPanel;
    }

    public SearchPanel getSearchPanel() {
        return leftPanel.getSearchPanel();
    }

    public LeftPanel getLeftPanel() {
        return leftPanel;
    }

    public BufferedImage getCurrentStopIcon() {
        return this.getMapPanel().getStopPainter().getCurrentIcon();
    }

    // BEHAVIOUR //
    public void setLogoutBehaviour(LogoutBehaviour behaviour) {
        leftPanel.setLogoutBehaviour(behaviour);
    }

    // Left Panel Behaviour //
    public void setFavStopBehaviour(FavoriteBehaviour behaviour) {
        this.stopPanel.setFavStopBehaviour(behaviour);
        this.leftPanel.setFavStopBehaviour(behaviour);
    }

    public void setFavRouteBehaviour(FavoriteBehaviour behaviour) {
        this.stopPanel.setFavRouteBehaviour(behaviour);
    }

    public void setButtonPanelGeneralBehaviour(ButtonPanelBehaviour listener) {
        this.leftPanel.setButtonPanelGeneralBehaviour(listener);
    }

    public void setButtonPanelPreferBehaviour(ButtonPanelBehaviour listener) {
        this.leftPanel.setButtonPanelPreferBehaviour(listener);
    }

    public void setSearchStopRowClickBehaviour(TableRowClickBehaviour listener) {
        leftPanel.setSearchStopRowClickBehaviour(listener);
    }

    public void setSearchRouteRowClickBehaviour(TableRowClickBehaviour listener) {
        leftPanel.setSearchRouteRowClickBehaviour(listener);
    }

    public void setTableCheckIsFavBehaviour(TableCheckIsFavBehaviour listener) {
        leftPanel.setTableCheckIsFavBehaviour(listener);
        stopPanel.setTableCheckIsFavBehaviour(listener);
    }

    public void setFavStopRowClickBehaviour(TableRowClickBehaviour behaviour) {
        this.leftPanel.setFavStopRowClickBehaviour(behaviour);
    }

    public void setFavRouteRowClickBehaviour(TableRowClickBehaviour behaviour) {
        this.leftPanel.setFavRouteRowClickBehaviour(behaviour);
    }

    public void setRealtimeSwitchListener(SwitchBarListener listener) {
        this.leftPanel.setRealtimeSwitchListener(listener);
    }

    public void setRealtimeStatus(RealtimeStatus status) {
        this.leftPanel.setRealtimeStatus(status);
    }
    // Waypoint Behaviour //

    public void setClickWaypointBehaviour(WaypointBehaviour waypointBehaviour) {
        this.mapPanel.setClickWaypointBehaviour(waypointBehaviour);
    }
    // Stop Panel Behaviour //

    public void setStopInfoRowClickBehaviour(TableRowClickBehaviour listener) {
        this.stopPanel.setStopInfoRowClickBehaviour(listener);
    }

    public void setStopRouteRowClickBehaviour(TableRowClickBehaviour listener) {
        this.stopPanel.setStopRouteRowClickBehaviour(listener);
    }

    public void updateStopPanelFavButtons(boolean favorite, String stop) {
        this.stopPanel.updateFavButtons(favorite, stop);
    }
    // Map Panel Behaviour //

    public void setMapPanelMapPosition(GeoPosition geoPosition, int zoomLevel) {
        this.mapPanel.setMapPanelMapPosition(geoPosition, zoomLevel);
    }

    public void setLocalMapCache(File cacheDir) {
        mapPanel.setLocalMapCache(cacheDir);
    }
}
