package com.nnamo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import com.nnamo.models.StopModel;

public class App {
    public static void main(String[] args) throws InterruptedException {
        Thread preloadThread = new Thread(() -> {
            try {
                DatabaseService db = new DatabaseService();
                StaticGtfsService gtfs = new StaticGtfsService();
                db.preloadGtfsData(gtfs);

                JXMapViewer mapViewer = new JXMapViewer();

                // Create a TileFactoryInfo for OpenStreetMap
                TileFactoryInfo info = new OSMTileFactoryInfo();
                DefaultTileFactory tileFactory = new DefaultTileFactory(info);
                mapViewer.setTileFactory(tileFactory);

                PanMouseInputListener mouseClick = new PanMouseInputListener(mapViewer);
                mapViewer.addMouseListener(mouseClick);
                mapViewer.addMouseMotionListener(mouseClick);
                ZoomMouseWheelListenerCenter mouseWheel = new ZoomMouseWheelListenerCenter(mapViewer);
                mapViewer.addMouseWheelListener(mouseWheel);

                // Use 8 threads in parallel to load the tiles
                tileFactory.setThreadPoolSize(8);

                // Set the focus
                GeoPosition rome = new GeoPosition(41.902782, 12.496366);
                GeoPosition rome2 = new GeoPosition(42.902782, 12.496366);

                mapViewer.setZoom(5);
                mapViewer.setAddressLocation(rome);

                // Display the viewer in a JFrame
                JFrame frame = new JFrame("Moovite");
                frame.getContentPane().add(mapViewer);
                frame.setSize(800, 600);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);

                // Create waypoints from the geo-positions
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
                mapViewer.setOverlayPainter(painter);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                System.err.println("Error loading GTFS data");
                e.printStackTrace();
                return;
            }

        });
        preloadThread.start();

        // Wait for the preload thread to finish before proceeding
        preloadThread.join();

        // MAP
        //////
        //////
        /////
        /////
        /////
        /////
    }
}
