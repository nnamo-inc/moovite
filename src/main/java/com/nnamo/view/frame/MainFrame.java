package com.nnamo.view.frame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.*;

import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.nnamo.enums.DataType;
import com.nnamo.enums.RealtimeStatus;
import com.nnamo.enums.UpdateMode;
import com.nnamo.interfaces.*;
import com.nnamo.models.StopModel;
import com.nnamo.models.StopTimeModel;
import com.nnamo.models.RealtimeStopUpdate;
import com.nnamo.models.RouteDirection;
import com.nnamo.view.components.*;
import org.jxmapviewer.viewer.GeoPosition;

public class MainFrame extends JFrame {
    private MapPanel mapPanel = new MapPanel();
    private StopPanel stopPanel = new StopPanel();
    private LeftPanel leftPanel = new LeftPanel();
    private PreferBar preferBar = new PreferBar();

    private JSplitPane splitMapStop;
    private JSplitPane splitLeftMap;

    public MainFrame() throws IOException {
        super("Moovite Map View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 800));
        setLayout(new BorderLayout());

        // Icona dell'app...
        try {
            setIconImage(
                    Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/application-bar-icon.png")));
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Icon not found, using default icon.");
        }

        // 1. Pannello che contiene PreferBar + Mappa + StopPanel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(preferBar, BorderLayout.SOUTH); // PreferBar sopra la mappa

        // 2. Split verticale tra mappa e stop panel
        splitMapStop = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mapPanel, stopPanel);
        splitMapStop.setResizeWeight(1.0);
        splitMapStop.setDividerSize(0);

        // 3. Aggiungi il split mappa-stop al pannello destro
        rightPanel.add(splitMapStop, BorderLayout.CENTER);

        // 4. Split orizzontale tra left panel e tutto il resto (prefer+mappa+stop)
        splitLeftMap = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitLeftMap.setResizeWeight(0.0);
        splitLeftMap.setDividerSize(0);

        // 5. Aggiungi solo il split principale
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

    public void renderRouteLines(List<StopModel> stopModels, List<VehiclePosition> routePositions, String routeId,
            GeoPosition geoPosition, int zoomLevel) {
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

    public void updateStopPanelRoutes(List<List<String>> uniqueRoutes) {
        this.stopPanel.updateStopRoutes(uniqueRoutes);
    }

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
    }

    public void updatePreferBarVisibility(boolean visible) {
        if (visible) {
            preferBar.open();
        } else {
            preferBar.close();
        }
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

    public void initLeftPanelPreferPanelPreferTable(List<StopModel> stops, List<RouteDirection> routes) {
        leftPanel.initPreferPanelPreferTable(stops, routes);
    }

    public void updateFavStopTable(StopModel stop, UpdateMode updateMode) {
        leftPanel.updateFavStopTable(stop, updateMode);
    }

    public void updateFavRouteTable(List<RouteDirection> route, UpdateMode updateMode) {
        leftPanel.updateFavRouteTable(route, updateMode);
    }

    public void updatePreferButton(String itemId, boolean isFav, DataType dataType) {
        this.preferBar.updatePreferButton(itemId, isFav, dataType);
    }

    public void removeRoutePainting() {
        this.mapPanel.removeRoutePainting();
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
    public void setGenericTableRowClickBehaviour(TableRowClickBehaviour listener) {
        this.leftPanel.setTableRowClickBehaviour(listener);
        this.stopPanel.setGenericTableRowClickBehaviour(listener);
    }

    public void setGeneralFavBehaviour(FavoriteBehaviour behaviour) {
        this.preferBar.setGeneralFavBehaviour(behaviour);
    }

    public void setButtonPanelGeneralBehaviour(ButtonPanelBehaviour listener) {
        this.leftPanel.setButtonPanelGeneralBehaviour(listener);
    }

    public void setButtonPanelPreferBehaviour(ButtonPanelBehaviour listener) {
        this.leftPanel.setButtonPanelPreferBehaviour(listener);
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

    public void setMapPanelMapPosition(GeoPosition geoPosition, int zoomLevel) {
        this.mapPanel.setMapPanelMapPosition(geoPosition, zoomLevel);
    }

    public void setLocalMapCache(File cacheDir) {
        mapPanel.setLocalMapCache(cacheDir);
    }
}
