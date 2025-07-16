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
    WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
    CompoundPainter<JXMapViewer> mapPainter = new CompoundPainter<JXMapViewer>();
    GeoPosition romePosition = new GeoPosition(41.902782, 12.496366);
    WaypointListener waypointListener;
    StopPainter stopPainter;
    TileFactory tileFactory;


    public MapPanel() throws IOException {
        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        tileFactory = new DefaultTileFactory(info);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        map.setTileFactory(tileFactory);
        map.setZoom(5);
        map.setAddressLocation(this.romePosition);

        List<org.jxmapviewer.painter.Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
        painters.add(this.waypointPainter);
        this.mapPainter.setPainters(painters);
        map.setOverlayPainter(mapPainter);

        this.stopPainter = new StopPainter(this.map, this.mapPainter, this.waypointPainter);

        setLayout(new BorderLayout());
        add(map, BorderLayout.CENTER);

        mapInputInit();
        clickOnWaypoint();
        zoomOnWaypoint();
    }

    public void renderStops(List<StopModel> stops) {
        Set<Waypoint> waypoints = new HashSet<Waypoint>();
        for (StopModel stop : stops) {
            waypoints.add(new DefaultWaypoint(stop.getLatitude(), stop.getLongitude()));
        }

        this.waypointPainter.setWaypoints(waypoints);
    }

    private void mapInputInit() throws IOException {
        PanMouseInputListener mouseClick = new PanMouseInputListener(map);
        ZoomMouseWheelListenerCursor mouseWheel = new ZoomMouseWheelListenerCursor(map);
        this.map.addMouseListener(mouseClick);
        this.map.addMouseMotionListener(mouseClick);
        this.map.addMouseWheelListener(mouseWheel);
    }

    private void clickOnWaypoint() { // TODO: Need to be checked
        this.map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (waypointListener != null) {
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
