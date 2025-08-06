package com.nnamo.view.components;

import com.google.transit.realtime.GtfsRealtime.Position;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.nnamo.interfaces.WaypointListener;
import com.nnamo.interfaces.ZoomBehaviour;
import com.nnamo.models.StopModel;
import com.nnamo.view.PositionPainter;
import com.nnamo.view.RoutePainter;
import com.nnamo.view.StopPainter;

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

public class MapPanel extends JPanel {
    private JXMapViewer map = new JXMapViewer();
    private GeoPosition romePosition = new GeoPosition(41.902782, 12.496366);
    private DefaultTileFactory tileFactory;

    // Waypoint painter and Map Painter
    private List<Painter<JXMapViewer>> stopsPaintersList;
    private List<Painter<JXMapViewer>> routePaintersList;

    private CompoundPainter<JXMapViewer> currentPainter;
    private CompoundPainter<JXMapViewer> stopsCompoundPainter;
    private CompoundPainter<JXMapViewer> routeCompoundPainter;

    private RoutePainter routePainter;
    private PositionPainter positionPainter;
    private StopPainter stopPainter;
    private StopPainter routeStopPainter;
    private ZoomBehaviour zoomBehaviour;

    private int currentZoomLimit; // Default zoom level
    private final int stopsZoomLimit = 4;
    private final int routeZoomLimit = 8;

    private JButton resetRouteButton = new JButton("Reset");
    private List<StopModel> stops;

    // Listener for waypoint clicks (Anonymous inner class in MapController)
    private WaypointListener waypointListener;

    public MapPanel() throws IOException {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        tileFactory = new DefaultTileFactory(info);
        map.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        map.setAddressLocation(this.romePosition);

        // Rendering of stops and vehicle positions icons on the map
        this.stopPainter = new StopPainter(this.map);
        this.routeStopPainter = new StopPainter(this.map);
        this.positionPainter = new PositionPainter(this.map);

        this.stopsCompoundPainter = new CompoundPainter<JXMapViewer>();
        this.routeCompoundPainter = new CompoundPainter<JXMapViewer>();
        this.currentPainter = stopsCompoundPainter;

        this.stopsPaintersList = createPaintersList(stopPainter);
        this.stopsCompoundPainter.setPainters(stopsPaintersList);

        this.routePaintersList = createPaintersList(positionPainter, routeStopPainter);
        this.routeCompoundPainter.setPainters(routePaintersList);

        setLayout(new BorderLayout());
        add(map, BorderLayout.CENTER);

        // General Zoom Behaviour
        zoomBehaviour = (new ZoomBehaviour() {
            @Override
            public void onZoomChange(int zoomLevel) {
                currentZoomLimit = (currentPainter == stopsCompoundPainter) ? stopsZoomLimit : routeZoomLimit;
                updateOverlayPainter();
                repaintView();
            }
        });
        setZoom(7);

        this.resetRouteButton.setVisible(false); // Hidden by default
        this.resetRouteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stops != null)
                    renderStops(stops);
            }
        });
        add(resetRouteButton, BorderLayout.SOUTH);

        handleMouseListeners();
        clickOnWaypoint();
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

    public void repaintView() {
        super.repaint();
        routeStopPainter.repaint();
        stopPainter.repaint();
        positionPainter.repaint();
        this.stopsCompoundPainter.setPainters(this.stopsPaintersList);
    }

    public void removeRoutePainting() {
        if (this.routePainter != null) {
            this.routePaintersList.remove(this.routePainter);
        }
        this.positionPainter.setWaypoints(new HashSet<Waypoint>());
        this.routeStopPainter.setWaypoints(new HashSet<Waypoint>());
        this.currentPainter = this.stopsCompoundPainter;
        updateOverlayPainter();
    }

    public void renderStops(List<StopModel> stops) {
        // Create a set of waypoints from the list of stops, then set it to the
        // waypointPainter
        Set<Waypoint> waypoints = new HashSet<Waypoint>();
        for (StopModel stop : stops) {
            waypoints.add(new DefaultWaypoint(stop.getLatitude(), stop.getLongitude()));
        }
        this.stopPainter.setWaypoints(waypoints);
        this.stops = stops; // Save stops in order to reset painting after route painting

        this.resetRouteButton.setVisible(false);
        this.currentPainter = stopsCompoundPainter;
        removeRoutePainting();
        repaintView();
    }

    public void renderStopsRoute(List<StopModel> stops) {
        System.out.println("MapPanel.renderStopsRoute() called with " + stops.size() + " stops");

        Set<Waypoint> waypoints = new HashSet<Waypoint>();
        for (StopModel stop : stops) {
            waypoints.add(new DefaultWaypoint(stop.getLatitude(), stop.getLongitude()));
        }
        this.routeStopPainter.setWaypoints(waypoints);

        // Delete older route painter in Painters List (if it exists)
        if (this.routePainter != null) {
            this.routePaintersList.remove(this.routePainter);
        }

        this.routePainter = new RoutePainter(stops, Color.RED, 5);
        this.routePaintersList.add(routePainter); // Adds updated route painter to the list
        this.routeCompoundPainter.setPainters(routePaintersList);
        this.currentPainter = routeCompoundPainter;

        this.resetRouteButton.setVisible(true);
        repaintView();
    }

    public void renderVehiclePositions(List<VehiclePosition> positions) {
        Set<Waypoint> waypoints = new HashSet<Waypoint>();
        for (VehiclePosition vehiclePosition : positions) {
            Position position = vehiclePosition.getPosition();
            waypoints.add(new DefaultWaypoint(position.getLatitude(), position.getLongitude()));
            System.out.println(
                    "Adding realtime vehicle position at " + position.getLatitude() + " " + position.getLongitude());
        }
        this.positionPainter.setWaypoints(waypoints);
        repaintView();
    }

    // Easily create painters list
    @SafeVarargs
    private List<Painter<JXMapViewer>> createPaintersList(Painter<JXMapViewer>... painters) {
        List<Painter<JXMapViewer>> paintersList = new ArrayList<Painter<JXMapViewer>>();
        for (Painter<JXMapViewer> painter : painters) {
            paintersList.add(painter);
        }
        return paintersList;
    }

    // Set the map to be draggable and zoomable with mouse and wheel listeners
    private void handleMouseListeners() throws IOException {
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
    }

    private void clickOnWaypoint() {
        // Add a personalized mouse listener to the map with an anonymous inner class
        // to handle clicks on waypoints only if the zoom level is less than 4
        this.map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (waypointListener != null && map.getZoom() <= currentZoomLimit) {
                    try {
                        GeoPosition geo = map.convertPointToGeoPosition(new Point(e.getX(), e.getY()));
                        waypointListener.onWaypointClick(geo, true);
                    } catch (SQLException | IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else {
                    try {
                        waypointListener.onWaypointClick(null, true);
                    } catch (SQLException | IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    public void setMapPanelMapPosition(GeoPosition geoPosition, int zoomLevel) {
        this.map.setAddressLocation(geoPosition);
        this.setZoom(zoomLevel);
    }

    // GETTERS AND SETTERS //
    public JXMapViewer getMap() {
        return map;
    }

    public StopPainter getStopPainter() {
        return this.stopPainter;
    }

    public TileFactory getTileFactory() {
        return tileFactory;
    }

    public void setWaypointListener(WaypointListener waypointListener) {
        this.waypointListener = waypointListener;
    }

    public void setZoom(int zoomLevel) {
        map.setZoom(zoomLevel);
        if (zoomBehaviour != null) {
            zoomBehaviour.onZoomChange(zoomLevel);
        }
    }

    public void increaseZoom(int offset) {
        this.setZoom(map.getZoom() + offset);
    }

    public void decreaseZoom(int offset) {
        this.setZoom(map.getZoom() - offset);
    }

    public void setLocalMapCache(File cacheDir) {
        boolean checkForUpdates = false;
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, checkForUpdates));
    }
}
