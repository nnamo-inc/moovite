package com.nnamo.view.components;

import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.nnamo.interfaces.WaypointListener;
import com.nnamo.interfaces.ZoomBehaviour;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopModel;
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
    JXMapViewer map = new JXMapViewer();
    GeoPosition romePosition = new GeoPosition(41.902782, 12.496366);
    DefaultTileFactory tileFactory;

    // Waypoint painter and Map Painter
    WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
    CompoundPainter<JXMapViewer> mapPainter;
    StopPainter stopPainter;
    ZoomBehaviour zoomBehaviour;

    JButton resetRouteButton = new JButton("Reset");
    List<StopModel> stops;

    // Route line painter
    RoutePainter routePainter;

    // Listener for waypoint clicks (Anonymous inner class in MapController)
    WaypointListener waypointListener;

    // CONSTRUCTOR //
    public MapPanel() throws IOException {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        tileFactory = new DefaultTileFactory(info);
        map.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        map.setZoom(5);
        map.setAddressLocation(this.romePosition);

        List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
        painters.add(this.waypointPainter);

        this.mapPainter = new CompoundPainter<JXMapViewer>();
        this.mapPainter.setPainters(painters);

        // Create a StopPainter instance to handle the stops on the map
        this.stopPainter = new StopPainter(this.map, this.mapPainter, this.waypointPainter);

        setLayout(new BorderLayout());
        add(map, BorderLayout.CENTER);

        // General Zoom Behaviour
        zoomBehaviour = (new ZoomBehaviour() {
            @Override
            public void onZoomChange(int zoomLevel) {
                map.setZoom(zoomLevel);
                stopPainter.repaint();
            }
        });

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
    public void renderStops(List<StopModel> stops) {
        // Create a set of waypoints from the list of stops, then set it to the
        // waypointPainter
        Set<Waypoint> waypoints = new HashSet<Waypoint>();
        for (StopModel stop : stops) {
            waypoints.add(new DefaultWaypoint(stop.getLatitude(), stop.getLongitude()));
        }
        List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
        this.waypointPainter.setWaypoints(waypoints);
        painters.add(this.waypointPainter); // Add waypoint painter on top
        this.mapPainter.setPainters(painters);

        this.stops = stops; // Save stops in order to reset painting after route painting
        this.resetRouteButton.setVisible(false);
    }

    public void renderStopsRoute(List<StopModel> stops) {
        System.out.println("MapPanel.renderStopsRoute() called with " + stops.size() + " stops");

        Set<Waypoint> waypoints = new HashSet<Waypoint>();
        for (StopModel stop : stops) {
            waypoints.add(new DefaultWaypoint(stop.getLatitude(), stop.getLongitude()));
        }
        this.waypointPainter.setWaypoints(waypoints);

        this.routePainter = new RoutePainter(stops, Color.RED, 5);
        System.out.println("Created RoutePainter with " + stops.size() + " stops");

        List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
        painters.add(this.routePainter); // Add route painter first (behind waypoints)
        painters.add(this.waypointPainter); // Add waypoint painter on top

        this.mapPainter.setPainters(painters);
        map.setOverlayPainter(this.mapPainter);

        System.out.println("Updated map painters");
        this.resetRouteButton.setVisible(true);
    }

    public void renderVehiclePositions(List<VehiclePosition> positions) {
        // TODO: implement positions rendering without overriding stops
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
                stopPainter.repaint();
            }
        });
    }

    private void clickOnWaypoint() {
        // Add a personalized mouse listener to the map with an anonymous inner class
        // to handle clicks on waypoints only if the zoom level is less than 4
        this.map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (waypointListener != null && map.getZoom() <= stopPainter.getZoomLimit()) {
                    try {
                        GeoPosition geo = map.convertPointToGeoPosition(new Point(e.getX(), e.getY()));
                        waypointListener.onWaypointClick(geo);
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
