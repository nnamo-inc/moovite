package com.nnamo.view;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;

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

public class MapView {

    JXMapViewer viewer = new JXMapViewer();
    WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
    CompoundPainter<JXMapViewer> mapPainter = new CompoundPainter<JXMapViewer>();
    GeoPosition romePosition = new GeoPosition(41.902782, 12.496366);

    private void init() {
        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        viewer.setTileFactory(tileFactory);
        viewer.setZoom(5);
        viewer.setAddressLocation(this.romePosition);

    }

    public void renderStops(List<StopModel> stops) {
        Set<Waypoint> waypoints = new HashSet<Waypoint>();
        for (StopModel stop : stops) {
            waypoints.add(new DefaultWaypoint(stop.getLatitude(), stop.getLongitude()));
        }
        this.waypointPainter.setWaypoints(waypoints);
    }

    private void handleMouse() {
        PanMouseInputListener mouseClick = new PanMouseInputListener(viewer);
        ZoomMouseWheelListenerCursor mouseWheel = new ZoomMouseWheelListenerCursor(viewer);
        this.viewer.addMouseListener(mouseClick);
        this.viewer.addMouseMotionListener(mouseClick);
        this.viewer.addMouseWheelListener(mouseWheel);
        this.viewer.addMouseWheelListener(new ZoomLevelListener(this.viewer, this.mapPainter));
    }

    public void run() {
        init();

        List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
        painters.add(this.waypointPainter);
        this.mapPainter.setPainters(painters);

        handleMouse();

        // TODO Needs to be extracted in a Frame class
        JFrame frame = new JFrame("Moovite");
        frame.getContentPane().add(viewer);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
