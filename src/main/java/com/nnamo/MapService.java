package com.nnamo;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import com.nnamo.models.StopModel;

public class MapService {

    JXMapViewer viewer = new JXMapViewer();
    DatabaseService db; // TODO Move to controller

    public MapService(DatabaseService db) {
        this.db = db;
    }

    private void init() {
        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        viewer.setTileFactory(tileFactory);
    }

    private void handleMouse() {
        PanMouseInputListener mouseClick = new PanMouseInputListener(viewer);
        ZoomMouseWheelListenerCursor mouseWheel = new ZoomMouseWheelListenerCursor(viewer);
        this.viewer.addMouseListener(mouseClick);
        this.viewer.addMouseMotionListener(mouseClick);
        this.viewer.addMouseWheelListener(mouseWheel);
    }

    public void run() throws SQLException {
        init();

        GeoPosition rome = new GeoPosition(41.902782, 12.496366);

        viewer.setZoom(5);
        viewer.setAddressLocation(rome);

        // TODO Move it to MapController
        Set<Waypoint> waypoints = new HashSet<Waypoint>();
        for (StopModel stop : db.getAllStops()) {
            waypoints.add(new DefaultWaypoint(stop.getLatitude(), stop.getLongitude()));
        }

        // Create a waypoint painter that takes all the waypoints
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
        waypointPainter.setWaypoints(waypoints);

        // Create a compound painter that uses both the route-painter and the
        // waypoint-painter
        List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
        painters.add(waypointPainter);

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);

        handleMouse();
        this.viewer.addMouseWheelListener(new ZoomLevelListener(viewer, painter));

        // Display the viewer in a JFrame
        JFrame frame = new JFrame("Moovite");
        frame.getContentPane().add(viewer);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
