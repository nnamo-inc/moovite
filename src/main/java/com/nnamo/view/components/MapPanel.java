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
    private List<Painter<JXMapViewer>> paintersList;
    private WaypointPainter<Waypoint> positionWaypoints = new WaypointPainter<Waypoint>();

    private CompoundPainter<JXMapViewer> mapPainter;
    private RoutePainter routePainter;
    private PositionPainter positionPainter;
    private StopPainter stopPainter;
    private ZoomBehaviour zoomBehaviour;

    private final int zoomLimit = 4;

    private JButton resetRouteButton = new JButton("Reset");
    private List<StopModel> stops;

    // Listener for waypoint clicks (Anonymous inner class in MapController)
    private WaypointListener waypointListener;

    // CONSTRUCTOR //
    public MapPanel() throws IOException {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        tileFactory = new DefaultTileFactory(info);
        map.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        map.setZoom(5);
        map.setAddressLocation(this.romePosition);

        // Rendering of stops and vehicle positions icons on the map
        this.stopPainter = new StopPainter(this.map);
        this.positionPainter = new PositionPainter(this.map);

        this.paintersList = createPaintersList(stopPainter, positionPainter);
        this.mapPainter = new CompoundPainter<JXMapViewer>();
        this.mapPainter.setPainters(paintersList);

        setLayout(new BorderLayout());
        add(map, BorderLayout.CENTER);

        // General Zoom Behaviour
        zoomBehaviour = (new ZoomBehaviour() {
            @Override
            public void onZoomChange(int zoomLevel) {
                map.setZoom(zoomLevel);
                stopPainter.repaint();
                positionPainter.repaint();

                if (zoomLevel <= zoomLimit) {
                    map.setOverlayPainter(mapPainter);
                } else if (zoomLevel > zoomLimit) {
                    map.setOverlayPainter(null);
                }
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
    private void repaintView() {
        stopPainter.repaint();
        positionPainter.repaint();
        this.mapPainter.setPainters(this.paintersList);
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
        repaintView();
    }

    public void renderStopsRoute(List<StopModel> stops) {
        System.out.println("MapPanel.renderStopsRoute() called with " + stops.size() + " stops");

        Set<Waypoint> waypoints = new HashSet<Waypoint>();
        for (StopModel stop : stops) {
            waypoints.add(new DefaultWaypoint(stop.getLatitude(), stop.getLongitude()));
        }
        this.stopPainter.setWaypoints(waypoints);

        this.routePainter = new RoutePainter(stops, Color.RED, 5);
        this.paintersList.add(routePainter);
        System.out.println("Created RoutePainter with " + stops.size() + " stops");

        System.out.println("Updated map painters");
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
        this.positionWaypoints.setWaypoints(waypoints);
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
                if (waypointListener != null && map.getZoom() <= zoomLimit) {
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
