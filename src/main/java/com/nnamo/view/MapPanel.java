package com.nnamo.view;

import com.nnamo.interfaces.WaypointListener;
import com.nnamo.models.StopModel;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapPanel extends JPanel {

    JXMapViewer map = new JXMapViewer();
    GeoPosition romePosition = new GeoPosition(41.902782, 12.496366);

    WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
    CompoundPainter<JXMapViewer> mapPainter = new CompoundPainter<JXMapViewer>();
    DefaultTileFactory tileFactory;

    WaypointListener waypointListener;
    StopPainter stopPainter;


    public MapPanel() throws IOException {
        // Create TileFactoryInfo(OpenStreetMap) to get tiles, then assign it to the DefaultTileFactory and finally set it to the map
        TileFactoryInfo info = new OSMTileFactoryInfo();
        tileFactory = new DefaultTileFactory(info);
        map.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        // Set map zoom and center position
        map.setZoom(5);
        map.setAddressLocation(this.romePosition);

        // Create a list of painters, add the waypointPainter to it, assign it to the mapPainter
        List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
        painters.add(this.waypointPainter);
        this.mapPainter.setPainters(painters);

        // Create a StopPainter instance to handle the stops on the map
        this.stopPainter = new StopPainter(this.map, this.mapPainter, this.waypointPainter);

        // Set the layout to show the map on the panel
        setLayout(new BorderLayout());
        add(map, BorderLayout.CENTER);

        // Initialize the map input listeners for panning and zooming
        mapInputInit();
        // Initialize the behavior for clicking on waypoints
        clickOnWaypoint();
        // Initialize the zoom behavior on waypoints
        zoomOnWaypoint();
    }

    public void renderStops(List<StopModel> stops) {
        // Create a set of waypoints from the list of stops, then set it to the waypointPainter
        Set<Waypoint> waypoints = new HashSet<Waypoint>();
        for (StopModel stop : stops) {
            waypoints.add(new DefaultWaypoint(stop.getLatitude(), stop.getLongitude()));
        }

        this.waypointPainter.setWaypoints(waypoints);
    }

    private void mapInputInit() throws IOException {
        // Set the map to be draggable and zoomable
        PanMouseInputListener mouseClick = new PanMouseInputListener(map);
        ZoomMouseWheelListenerCursor mouseWheel = new ZoomMouseWheelListenerCursor(map);
        this.map.addMouseListener(mouseClick);
        this.map.addMouseMotionListener(mouseClick);
        this.map.addMouseWheelListener(mouseWheel);
    }

    private void clickOnWaypoint() {
        // Add a mouse listener to the map to handle clicks on waypoints only if the zoom level is less than 4
        this.map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (waypointListener != null && map.getZoom() <= stopPainter.getZoomLimit()) {
                    try {
                        GeoPosition geo = map.convertPointToGeoPosition(new Point(e.getX(), e.getY()));
                        waypointListener.waypointClicked(geo);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    public void zoomOnWaypoint() {
        this.map.addMouseWheelListener(stopPainter.getZoomLevelListener()); }

    public void setWaypointListener(WaypointListener waypointListener) {
        this.waypointListener = waypointListener;
    }

    public JXMapViewer getMap() {
        return map;
    }

    public StopPainter getStopPainter() {
        return this.stopPainter;
    }

    public TileFactory getTileFactory() {
        return tileFactory;
    }
}
