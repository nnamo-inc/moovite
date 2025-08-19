package com.nnamo.view.components;

import com.google.transit.realtime.GtfsRealtime.Position;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.nnamo.interfaces.WaypointBehaviour;
import com.nnamo.interfaces.ZoomBehaviour;
import com.nnamo.models.StopModel;
import com.nnamo.view.painter.PositionPainter;
import com.nnamo.view.painter.RoutePainter;
import com.nnamo.view.painter.StopPainter;
import com.nnamo.view.waypoints.StopWaypoint;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Custom {@link JPanel} that displays an interactive map using {@link JXMapViewer}, supporting the rendering of stops, routes, and real-time vehicle positions.
 *
 * @author Riccardo Finocchiaro
 * @author Samuele Lombardi
 *
 * @see JPanel
 * @see JXMapViewer
 * @see StopModel
 * @see VehiclePosition
 * @see WaypointBehaviour
 * @see ZoomBehaviour
 * @see StopPainter
 * @see RoutePainter
 * @see PositionPainter
 */
public class MapPanel extends JPanel {

    // ATTRIBUTES //
    private JXMapViewer map = new JXMapViewer();
    private final GeoPosition ROMEPOSITION = new GeoPosition(41.902782, 12.496366);
    private DefaultTileFactory tileFactory;

    private List<Painter<JXMapViewer>> stopsPaintersList;
    private List<Painter<JXMapViewer>> routePaintersList;

    private CompoundPainter<JXMapViewer> currentPainter;
    private CompoundPainter<JXMapViewer> stopsCompoundPainter;
    private CompoundPainter<JXMapViewer> routeCompoundPainter;

    private String currentRouteId;
    private String currentStopId;
    private GeoPosition stopPosition;

    private RoutePainter routePainter;
    private PositionPainter positionPainter;
    private StopPainter stopPainter;
    private StopPainter routeStopPainter;
    private ZoomBehaviour zoomBehaviour;

    private int currentZoomLimit; // Default zoom level
    private final int STOPSZOOMLIMIT = 4;
    private final int ROUTESZOOMLIMIT = 8;

    private JButton resetRouteButton;
    private List<StopModel> stops;

    private WaypointBehaviour waypointBehaviour;

    /**
     * Creates a {@link MapPanel} with an embedded {@link JXMapViewer}, initializing painters for stops, routes, and vehicle positions.
     * Sets up the map tile factory, default position, zoom, and listeners for user interaction.
     *
     * @throws IOException if there is an error initializing the map tile factory
     *
     * @see JPanel
     * @see JXMapViewer
     * @see StopPainter
     * @see RoutePainter
     * @see PositionPainter
     */
    public MapPanel() throws IOException {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        tileFactory = new DefaultTileFactory(info);
        map.setTileFactory(tileFactory);

        tileFactory.setThreadPoolSize(8); // Use 8 threads in parallel to load the tiles

        map.setAddressLocation(this.ROMEPOSITION);
        map.setZoom(7);

        // Rendering of stops and vehicle positions icons on the map
        this.stopPainter = new StopPainter(this.map);
        this.routePainter = new RoutePainter();
        this.routeStopPainter = new StopPainter(this.map);
        this.positionPainter = new PositionPainter(this.map);

        this.stopsCompoundPainter = new CompoundPainter<JXMapViewer>();
        this.routeCompoundPainter = new CompoundPainter<JXMapViewer>();

        this.stopsPaintersList = createPaintersList(stopPainter);
        this.stopsCompoundPainter.setPainters(stopsPaintersList);

        this.routePaintersList = createPaintersList(positionPainter, routeStopPainter, routePainter);
        this.routeCompoundPainter.setPainters(routePaintersList);

        setLayout(new BorderLayout());
        add(map, BorderLayout.CENTER);

        zoomBehaviour = (new ZoomBehaviour() {
            @Override
            public void onZoomChange(int zoomLevel) {
                updateOverlayPainter();
                repaintView();
            }
        });

        resetRouteButton = new JButton("Reset");
        this.resetRouteButton.setVisible(false);

        add(resetRouteButton, BorderLayout.SOUTH);
        initListeners();
    }

    // BEHAVIOUR METHODS //
    private void initListeners() {
        PanMouseInputListener mouseClick = new PanMouseInputListener(map);
        this.map.addMouseListener(mouseClick);
        this.map.addMouseMotionListener(mouseClick);
        this.map.addMouseWheelListener(new ZoomMouseWheelListenerCursor(map) {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                super.mouseWheelMoved(e);
                zoomBehaviour.onZoomChange(map.getZoom());
            }
        });

        resetRouteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stops != null)
                    renderStops(stops);
                updateOverlayPainter();
                repaintView();
            }
        });

        this.map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (waypointBehaviour != null && map.getZoom() <= currentZoomLimit) {
                    try {
                        GeoPosition geo = map.convertPointToGeoPosition(new Point(e.getX(), e.getY()));
                        waypointBehaviour.onWaypointClick(geo, true);
                    } catch (SQLException | IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    try {
                        waypointBehaviour.onWaypointClick(null, true);
                    } catch (SQLException | IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

    }

    /**
     * Sets the behaviour for waypoint clicks, allowing custom actions when a waypoint is clicked.
     *
     * @param waypointBehaviour the {@link WaypointBehaviour} to set
     */
    public void setClickWaypointBehaviour(WaypointBehaviour waypointBehaviour) {
        this.waypointBehaviour = waypointBehaviour;
    }

    // METHODS //
    private void updateOverlayPainter() {
        int zoomLevel = map.getZoom();
        if (zoomLevel <= currentZoomLimit) {
            map.setOverlayPainter(currentPainter);
        } else {
            map.setOverlayPainter(null);
        }
    }

    /**
     * Repaints the map and all overlay painters, updating the display of stops, routes, and vehicle positions.
     *
     * @see JXMapViewer
     * @see StopPainter
     * @see RoutePainter
     * @see PositionPainter
     */
    public void repaintView() {
        super.repaint();
        routeStopPainter.repaint();
        positionPainter.repaint();

        if (currentStopId != null) {
            stopPainter.repaint(currentStopId);
        } else {
            stopPainter.repaint();
        }
        this.stopsCompoundPainter.setPainters(this.stopsPaintersList);
        updateOverlayPainter();
    }

    private void updateCurrentCompoundPainter(CompoundPainter<JXMapViewer> painter) {
        System.out.println("Updated compound painter");
        this.currentZoomLimit = (painter == stopsCompoundPainter) ? STOPSZOOMLIMIT : ROUTESZOOMLIMIT;
        this.currentPainter = painter;

        if (painter == routeCompoundPainter) {
            this.resetRouteButton.setVisible(true);
        } else {
            this.resetRouteButton.setVisible(false);
        }
    }

    /**
     * Resets the map to its default state, clearing route overlays and restoring the stop view.
     * Hides the reset button and resets the painter overlays.
     *
     * @see JXMapViewer
     * @see StopPainter
     * @see RoutePainter
     * @see PositionPainter
     */
    public void resetAction() {
        this.resetRouteButton.setVisible(false);
        if (this.routePainter != null) {
            this.routePaintersList.remove(this.routePainter);
        }

        if (this.stopPosition != null) {
            map.setAddressLocation(this.stopPosition);
            map.setZoom(2);
        }

        this.positionPainter.setWaypoints(new HashSet<Waypoint>());
        this.routeStopPainter.setWaypoints(new HashSet<Waypoint>());
        this.currentRouteId = null;
        updateCurrentCompoundPainter(stopsCompoundPainter);
        updateOverlayPainter();
        repaintView();
    }

    /**
     * Renders the provided list of {@link StopModel} as waypoints on the map.
     * Updates the overlay painter and saves the stops for future resets.
     *
     * @param stops the list of stops to display as waypoints
     *
     * @see StopModel
     * @see StopPainter
     */
    public void renderStops(List<StopModel> stops) {
        // Create a set of waypoints from the list of stops, then set it to the
        // waypointPainter
        Set<Waypoint> waypoints = new HashSet<Waypoint>();
        for (StopModel stop : stops) {
            waypoints.add(new StopWaypoint(stop.getId(), stop.getLatitude(), stop.getLongitude()));
        }
        this.stopPainter.setWaypoints(waypoints);
        this.stops = stops; // Save stops in order to reset painting after route painting

        resetAction();
        repaintView();
    }

    /**
     * Renders the provided list of {@link StopModel} as a route on the map, updating the route overlay and painter.
     *
     * @param stops the list of stops representing the route to display
     *
     * @see StopModel
     * @see RoutePainter
     * @see StopPainter
     */
    public void renderStopsRoute(List<StopModel> stops) {
        System.out.println("MapPanel.renderStopsRoute() called with " + stops.size() + " stops");

        Set<StopWaypoint> waypoints = new HashSet<StopWaypoint>();
        for (StopModel stop : stops) {
            waypoints.add(new StopWaypoint(stop.getId(), stop.getLatitude(), stop.getLongitude()));
        }
        this.routeStopPainter.setWaypoints(waypoints);

        // Delete older route painter in Painters List (if it exists)
        if (this.routePainter != null) {
            this.routePaintersList.remove(this.routePainter);
        }

        this.routePainter.setStops(stops);
        this.routePaintersList.add(routePainter); // Adds updated route painter to the list
        this.routeCompoundPainter.setPainters(routePaintersList);

        updateCurrentCompoundPainter(this.routeCompoundPainter);
        updateOverlayPainter();
        repaintView();
    }

    /**
     * Renders the provided list of {@link VehiclePosition} as real-time vehicle waypoints on the map.
     *
     * @param positions the list of vehicle positions to display
     *
     * @see VehiclePosition
     * @see PositionPainter
     */
    public void renderVehiclePositions(List<VehiclePosition> positions) {
        Set<Waypoint> waypoints = new HashSet<Waypoint>();
        for (VehiclePosition vehiclePosition : positions) {
            Position position = vehiclePosition.getPosition();
            waypoints.add(new DefaultWaypoint(position.getLatitude(), position.getLongitude()));
            System.out.println(
                    "Adding realtime vehicle position at " + position.getLatitude() + " " + position.getLongitude());
        }
        this.positionPainter.setWaypoints(waypoints);
        updateCurrentCompoundPainter(this.routeCompoundPainter);
        repaintView();
    }

    @SafeVarargs
    private List<Painter<JXMapViewer>> createPaintersList(Painter<JXMapViewer>... painters) {
        List<Painter<JXMapViewer>> paintersList = new ArrayList<Painter<JXMapViewer>>();
        for (Painter<JXMapViewer> painter : painters) {
            paintersList.add(painter);
        }
        return paintersList;
    }

    // GETTERS AND SETTERS //
    /**
     * Gets the current route ID.
     *
     * @return the current route ID
     */
    public String getCurrentRouteId() {
        return this.currentRouteId;
    }

    /**
     * Gets the current stop position as a {@link GeoPosition}.
     *
     * @return the current stop position
     */
    public GeoPosition getCurrentStopPosition() {
        return this.stopPosition;
    }

    /**
     * Sets the current route ID and updates the map view accordingly.
     *
     * @param routeId the ID of the current route
     */
    public void setCurrentRouteId(String routeId) {
        System.out.println("Updated current route id to " + routeId);
        this.currentRouteId = routeId;
    }

    /**
     * Gets the current stop ID.
     *
     * @return the current stop ID
     */
    public String getCurrentStopId() {
        return this.currentStopId;
    }

    /**
     * Sets the current stop position on the map.
     *
     * @param position the {@link GeoPosition} of the current stop
     */
    public void setCurrentStopPosition(GeoPosition position) {
        this.stopPosition = position;
    }

    /**
     * Sets the current stop ID and position, updating the map view accordingly.
     *
     * @param stopId the ID of the current stop
     */
    public void setCurrentStopId(String stopId) {
        this.currentStopId = stopId;
    }

    /**
     * Sets the current stop ID and position, updating both the ID and position attributes.
     *
     * @param stopId the ID of the current stop
     * @param position the {@link GeoPosition} of the current stop
     */
    public void setCurrentStop(String stopId, GeoPosition position) {
        this.setCurrentStopId(stopId);
        this.setCurrentStopPosition(position);
    }

    /**
     * Gets the underlying {@link JXMapViewer} instance used in this panel.
     *
     * @return the JXMapViewer instance
     */
    public JXMapViewer getMap() {
        return map;
    }

    /**
     * Gets the {@link StopPainter} instance used for rendering stops on the map.
     *
     * @return the StopPainter instance
     */
    public StopPainter getStopPainter() {
        return this.stopPainter;
    }

    /**
     * Sets the map position and zoom level for the map panel.
     *
     * @param geoPosition the {@link GeoPosition} to set as the map center
     * @param zoomLevel the zoom level to set for the map
     */
    public void setMapPanelMapPosition(GeoPosition geoPosition, int zoomLevel) {
        this.map.setAddressLocation(geoPosition);
        this.setZoom(zoomLevel);
    }

    /**
     * Sets the zoom level for the map and notifies the zoom behaviour if set.
     *
     * @param zoomLevel the zoom level to set for the map
     */
    public void setZoom(int zoomLevel) {
        map.setZoom(zoomLevel);
        if (zoomBehaviour != null) {
            zoomBehaviour.onZoomChange(zoomLevel);
        }
    }

    /**
     * Sets the local cache directory for the map tile factory, allowing offline tile storage.
     *
     * @param cacheDir the directory to use for caching map tiles
     */
    public void setLocalMapCache(File cacheDir) {
        boolean checkForUpdates = false;
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, checkForUpdates));
    }
}
