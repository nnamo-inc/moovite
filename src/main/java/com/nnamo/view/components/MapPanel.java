package com.nnamo.view.components;

import com.nnamo.interfaces.WaypointListener;
import com.nnamo.interfaces.ZoomBehaviour;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopModel;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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
        this.waypointPainter.setWaypoints(waypoints);
    }

    public void renderRoute(RouteModel route) {
        // TODO Implement route render
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
        boolean checkForUpdates = true;
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, true));
    }

}
