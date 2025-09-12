package com.nnamo.view.frame;

import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.nnamo.enums.DataType;
import com.nnamo.enums.RealtimeMetricType;
import com.nnamo.enums.RealtimeStatus;
import com.nnamo.enums.UpdateMode;
import com.nnamo.interfaces.*;
import com.nnamo.models.*;
import com.nnamo.services.DatabaseService;
import com.nnamo.services.RealtimeGtfsService;
import com.nnamo.utils.Log;
import com.nnamo.view.customcomponents.statistic.MetricCollector;
import com.nnamo.view.custompanels.*;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Custom {@link JFrame} that composes and manages the primary UI panels,
 * including the map, stop details, favorites bar, and navigation panels.
 *
 * @author Riccardo Finocchiaro
 * @author Samuele Lombardi
 * @author Davide Galilei
 * @see JFrame
 * @see MapPanel
 * @see StopPanel
 * @see LeftPanel
 * @see PreferBarPanel
 * @see SearchPanel
 * @see DataType
 * @see UpdateMode
 * @see RealtimeStatus
 * @see WaypointBehaviour
 * @see TableRowClickBehaviour
 * @see FavoriteBehaviour
 * @see ButtonPanelBehaviour
 * @see SwitchBarListener
 * @see SearchBarListener
 */
public class MainFrame extends JFrame {
    private final MapPanel mapPanel = new MapPanel();
    private final StopPanel stopPanel = new StopPanel();
    private final LeftPanel leftPanel = new LeftPanel();
    private final PreferBarPanel preferBarPanel = new PreferBarPanel();

    private final JSplitPane splitMapStop;
    private final JSplitPane splitLeftMap;

    /**
     * Constructs the main application frame, initializing all UI panels and layout.
     * Sets up split panes for navigation and content, and configures the
     * application icon.
     *
     * @throws IOException if an error occurs during map panel initialization
     * @see JFrame
     * @see MapPanel
     * @see StopPanel
     * @see LeftPanel
     * @see PreferBarPanel
     */
    public MainFrame() throws IOException {
        super("Moovite Map View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setMinimumSize(new Dimension(900, 600));
        setLayout(new BorderLayout());

        // Icon app
        try {
            setIconImage(
                    Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/application-bar-icon.png")));
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.warn("Icon not found, using default icon");
        }

        // Map Panel + Stop Panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(preferBarPanel, BorderLayout.SOUTH); // PreferBar sopra la mappa

        // Vertical Spilt beetween Map Panel and Stop Panel
        splitMapStop = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mapPanel, stopPanel);
        splitMapStop.setResizeWeight(1.0);
        splitMapStop.setDividerSize(0);

        // Add vertical split to the right panel
        rightPanel.add(splitMapStop, BorderLayout.CENTER);

        // Horizontal split beetween left panel and everything else
        splitLeftMap = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitLeftMap.setResizeWeight(0.0);
        splitLeftMap.setDividerSize(0);

        // Set the size of the main frame to the screen size
        add(splitLeftMap, BorderLayout.CENTER);
    }

    // METHODS //

    /**
     * Makes the main frame visible.
     */
    public void open() {
        setVisible(true);
    }

    /**
     * Hides the main frame.
     */
    public void close() {
        setVisible(false);
    }

    /**
     * Renders the provided list of stops on the map panel.
     *
     * @param stops the list of stops to display
     * @see StopModel
     * @see MapPanel
     */
    public void renderStops(List<StopModel> stops) {
        mapPanel.renderStops(stops);
    }

    /**
     * Returns the current stop position from the map panel.
     *
     * @return the current stop {@link GeoPosition}
     * @see GeoPosition
     */
    public GeoPosition getCurrentStopPosition() {
        return mapPanel.getCurrentStopPosition();
    }

    /**
     * Sets the current stop ID and position in the map panel.
     *
     * @param stopId   the stop ID to set
     * @param position the {@link GeoPosition} to set
     * @see GeoPosition
     */
    public void setCurrentStop(String stopId, GeoPosition position) {
        this.mapPanel.setCurrentStop(stopId, position);
    }

    /**
     * Repaints the map panel and its overlays.
     */
    public void repaintMap() {
        this.mapPanel.repaintView();
    }

    /**
     * Renders route lines and vehicle positions on the map panel, and updates the
     * current route and map position.
     *
     * @param stopModels        the list of stops representing the route
     * @param realtimePositions the list of vehicle positions
     * @param routeId           the route ID to set
     * @param geoPosition       the map position to center on
     * @param zoomLevel         the zoom level to set
     * @see StopModel
     * @see VehiclePosition
     * @see GeoPosition
     */
    public void renderRouteLines(List<StopModel> stopModels, List<VehiclePosition> realtimePositions,
                                 List<StaticVehiclePosition> staticPositions, String routeId,
                                 GeoPosition geoPosition, int zoomLevel) {
        mapPanel.renderStopsRoute(stopModels);
        mapPanel.renderVehiclePositions(realtimePositions, staticPositions);
        mapPanel.repaintView();
        setCurrentRouteId(routeId);
        if (geoPosition != null) {
            setMapPanelMapPosition(geoPosition, zoomLevel);
        }
    }

    /**
     * Renders the provided vehicle positions on the map panel.
     *
     * @param realtimePositions the list of real-time vehicle positions to display
     * @param staticPositions   the list of static vehicle positions to display if no
     *                          real-time data is available
     * @see VehiclePosition
     */
    public void renderVehiclePositions(List<VehiclePosition> realtimePositions,
                                       List<com.nnamo.models.StaticVehiclePosition> staticPositions) {
        mapPanel.renderVehiclePositions(realtimePositions, staticPositions);
    }

    private Dimension getScreenSizes() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * Updates the stop panel with stop information.
     *
     * @param id   the stop ID
     * @param nome the stop name
     */
    public void updateStopPanelInfo(String id, String nome) {
        this.stopPanel.updateStopInfo(id, nome);
    }

    /**
     * Updates the stop panel with stop times and real-time updates.
     *
     * @param stopTimes       the list of stop times
     * @param realtimeUpdates the list of real-time updates
     * @see StopTimeModel
     * @see RealtimeStopUpdate
     */
    public void updateStopPanelTimes(List<StopTimeModel> stopTimes, List<RealtimeStopUpdate> realtimeUpdates) {
        this.stopPanel.updateStopTimes(stopTimes, realtimeUpdates);
    }

    /**
     * Updates the stop panel with available routes.
     *
     * @param uniqueRoutes the list of unique routes
     */
    public void updateStopPanelRoutes(List<List<String>> uniqueRoutes) {
        this.stopPanel.updateStopRoutes(uniqueRoutes);
    }

    /**
     * Sets the visibility of the stop panel.
     *
     * @param visible true to show, false to hide
     */
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

    /**
     * Sets the visibility of the favorites bar.
     *
     * @param visible true to show, false to hide
     */
    public void updatePreferBarVisibility(boolean visible) {
        if (visible) {
            preferBarPanel.open();
        } else {
            preferBarPanel.close();
        }
    }

    /**
     * Sets the visibility of the favorites bar.
     *
     * @param visible true to show, false to hide
     */
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

    /**
     * Updates the modular panel in the left panel.
     *
     * @param panel     the panel to update
     * @param isVisible true to show, false to hide
     */
    public void updateLeftPanelModularPanel(JPanel panel, boolean isVisible) {
        leftPanel.updateModularPanel(panel, isVisible);
    }

    /**
     * Updates the favorite button in the favorites bar.
     *
     * @param itemId   the item ID
     * @param isFav    true if favorite, false otherwise
     * @param dataType the data type (STOP or ROUTE)
     * @see DataType
     */
    public void updatePreferButton(String itemId, boolean isFav, DataType dataType) {
        this.preferBarPanel.updatePreferButton(itemId, isFav, dataType);
    }

    /**
     * Removes the route painting from the map panel.
     */
    public void removeRoutePainting() {
        this.mapPanel.resetAction();
    }

    // GETTERS AND SETTERS //

    /**
     * Returns the map panel instance.
     *
     * @return the {@link MapPanel}
     */
    public MapPanel getMapPanel() {
        return mapPanel;
    }

    /**
     * Returns the current stop ID from the map panel.
     *
     * @return the current stop ID
     */
    public String getCurrentStopId() {
        return mapPanel.getCurrentStopId();
    }

    /**
     * Returns the current route ID from the map panel.
     *
     * @return the current route ID
     */
    public String getCurrentRouteId() {
        return mapPanel.getCurrentRouteId();
    }

    /**
     * Sets the current route ID in the map panel.
     *
     * @param routeId the route ID to set
     */
    public void setCurrentRouteId(String routeId) {
        mapPanel.setCurrentRouteId(routeId);
    }

    /**
     * Returns the stop panel instance.
     *
     * @return the {@link StopPanel}
     */
    public StopPanel getStopPanel() {
        return stopPanel;
    }

    /**
     * Returns the current stop icon from the map panel's stop painter.
     *
     * @return the current stop {@link BufferedImage} icon
     */
    public BufferedImage getCurrentStopIcon() {
        return this.getMapPanel().getStopPainter().getCurrentIcon();
    }

    /**
     * Sets the map panel's position and zoom level.
     *
     * @param geoPosition the position to center the map on
     * @param zoomLevel   the zoom level to set
     * @see GeoPosition
     */
    public void setMapPanelMapPosition(GeoPosition geoPosition, int zoomLevel) {
        this.mapPanel.setMapPanelMapPosition(geoPosition, zoomLevel);
    }

    /**
     * Sets the local cache directory for map tiles in the map panel.
     *
     * @param cacheDir the cache directory
     * @see File
     */
    public void setLocalMapCache(File cacheDir) {
        mapPanel.setLocalMapCache(cacheDir);
    }

    // TRANSIT METHODS //

    /**
     * Initializes the favorites table in the left panel's prefer panel.
     *
     * @param stops  the list of favorite stops
     * @param routes the list of favorite routes
     * @see StopModel
     * @see RouteDirection
     */
    public void initLeftPanelPreferPanelPreferTable(List<StopModel> stops, List<RouteDirection> routes) {
        leftPanel.initPreferPanelPreferTable(stops, routes);
    }

    /**
     * Updates the favorite stops table in the left panel.
     *
     * @param stop       the stop to update
     * @param updateMode the update mode (ADD or REMOVE)
     * @see StopModel
     * @see UpdateMode
     */
    public void updateFavStopTable(StopModel stop, UpdateMode updateMode) {
        leftPanel.updateFavStopTable(stop, updateMode);
    }

    /**
     * Updates the favorite routes table in the left panel.
     *
     * @param route      the list of routes to update
     * @param updateMode the update mode (ADD or REMOVE)
     * @see RouteDirection
     * @see UpdateMode
     */
    public void updateFavRouteTable(List<RouteDirection> route, UpdateMode updateMode) {
        leftPanel.updateFavRouteTable(route, updateMode);
    }

    /**
     * Sets the logout behavior in the left panel.
     *
     * @param behaviour the logout behavior implementation
     * @see LogoutBehaviour
     */
    public void setLogoutBehaviour(LogoutBehaviour behaviour) {
        leftPanel.setLogoutBehaviour(behaviour);
    }

    /**
     * Sets the waypoint click behavior in the map panel.
     *
     * @param waypointBehaviour the waypoint behavior implementation
     * @see WaypointBehaviour
     */
    public void setClickWaypointBehaviour(WaypointBehaviour waypointBehaviour) {
        this.mapPanel.setClickWaypointBehaviour(waypointBehaviour);
    }

    /**
     * Sets the real-time status in the left panel.
     *
     * @param status the real-time status
     * @see RealtimeStatus
     */
    public void setRealtimeStatus(RealtimeStatus status) {
        this.leftPanel.setRealtimeStatus(status);
    }

    /**
     * Sets the generic table row click behavior for the left and stop panels.
     *
     * @param listener the table row click behavior implementation
     * @see TableRowClickBehaviour
     */
    public void setGenericTableRowClickBehaviour(TableRowClickBehaviour listener) {
        this.leftPanel.setTableRowClickBehaviour(listener);
        this.stopPanel.setGenericTableRowClickBehaviour(listener);
    }

    /**
     * Sets the general favorite behavior in the favorites bar.
     *
     * @param behaviour the favorite behavior implementation
     * @see FavoriteBehaviour
     */
    public void setGeneralFavBehaviour(FavoriteBehaviour behaviour) {
        this.preferBarPanel.setGeneralFavBehaviour(behaviour);
    }

    /**
     * Sets the general button panel behavior in the left panel.
     *
     * @param listener the button panel behavior implementation
     * @see ButtonPanelBehaviour
     */
    public void setButtonPanelGeneralBehaviour(ButtonPanelBehaviour listener) {
        this.leftPanel.setButtonPanelGeneralBehaviour(listener);
    }

    /**
     * Sets the prefer button panel behavior in the left panel.
     *
     * @param listener the button panel behavior implementation
     * @see ButtonPanelBehaviour
     */
    public void setButtonPanelPreferBehaviour(ButtonPanelBehaviour listener) {
        this.leftPanel.setButtonPanelPreferBehaviour(listener);
    }

    /**
     * Sets the real-time switch listener in the left panel.
     *
     * @param listener the switch bar listener implementation
     * @see SwitchBarListener
     */
    public void setRealtimeSwitchListener(SwitchBarListener listener) {
        this.leftPanel.setRealtimeSwitchListener(listener);
    }

    /**
     * Sets the search panel listener in the left panel.
     *
     * @param listener the search bar listener implementation
     * @see SearchBarListener
     */
    public void setSearchPanelListener(SearchBarListener listener) {
        this.leftPanel.setSearchPanelListener(listener);
    }

    /**
     * Sets the prefer panel listener in the left panel.
     *
     * @param listener the search bar listener implementation
     * @see SearchBarListener
     */
    public void setPreferPanelListener(SearchBarListener listener) {
        this.leftPanel.setPreferPanelListener(listener);
    }

    /**
     * Sets up the statistics panel in the left panel with the provided services.
     *
     * @param realtimeService the real-time GTFS service
     * @param metricsMap      the map of real-time metrics
     * @param collector       the metrics collector
     * @see RealtimeGtfsService
     * @see DatabaseService
     */
    public void setupStatisticsPanel(RealtimeGtfsService realtimeService, Map<RealtimeMetricType, List<RealtimeMetricModel>> metricsMap, MetricCollector collector) {
        this.leftPanel.setupStatisticsPanel(realtimeService, metricsMap, collector);
    }

    /**
     * Clears the favorites table in the prefer panel.
     */
    public void clearPreferPanelTable() {
        leftPanel.clearPreferPanelTable();
    }

    /**
     * Initializes the favorites table in the prefer panel with the provided stops
     * and routes.
     *
     * @param favoriteStops  the list of favorite stops
     * @param favoriteRoutes the list of favorite routes
     * @see StopModel
     * @see RouteDirection
     */
    public void initPreferPanelTable(List<StopModel> favoriteStops, List<RouteDirection> favoriteRoutes) {
        leftPanel.initPreferTable(favoriteStops, favoriteRoutes);
    }

    /**
     * Renders the search panel with the provided stops and routes.
     *
     * @param stops  the list of stops to display
     * @param routes the list of routes to display
     * @see StopModel
     * @see RouteDirection
     */
    public void renderSearchPanel(List<StopModel> stops, List<RouteDirection> routes) {
        leftPanel.renderSearchPanel(stops, routes);
    }
}
