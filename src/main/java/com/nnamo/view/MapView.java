package com.nnamo.view;

import com.nnamo.interfaces.WaypointListener;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.viewer.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;

import com.nnamo.models.StopModel;

public class MapView {

    JXMapViewer viewer = new JXMapViewer();

    WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
    CompoundPainter<JXMapViewer> mapPainter = new CompoundPainter<JXMapViewer>();

    TileFactory tileFactory;
    GeoPosition romePosition = new GeoPosition(41.902782, 12.496366);

    WaypointListener waypointListener;

    private void initMap() {
        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        tileFactory = new DefaultTileFactory(info);

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

        // - - - - - - - - personal icon, need to be checked - - - - - - - - //
        try {
            final BufferedImage icon = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/waypoint_white.png")));
            this.waypointPainter.setRenderer(new WaypointRenderer<Waypoint>() {
                @Override
                public void paintWaypoint(Graphics2D g, JXMapViewer viewer, Waypoint waypoint) {
                    Point2D point = viewer.getTileFactory().geoToPixel(waypoint.getPosition(), viewer.getZoom());
                    int x = (int) point.getX() - icon.getWidth() / 2;
                    int y = (int) point.getY() - icon.getHeight();
                    g.drawImage(icon, x, y, null);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            this.waypointPainter.setRenderer(new DefaultWaypointRenderer());
        }
        // - - - - - - - - - - - - - - - - //

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

    public void setWaypointListener(WaypointListener waypointListener) {
        this.waypointListener = waypointListener;
    }

    private void waypointPopUp() { // TODO: Need to be checked
        this.viewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (waypointListener != null) {
                    try {
                        GeoPosition geo = viewer.convertPointToGeoPosition(new Point(e.getX(), e.getY()));
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

    public void run() {
        initMap();

        List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
        painters.add(this.waypointPainter);
        this.mapPainter.setPainters(painters);

        handleMouse();
        waypointPopUp();

        // TODO Needs to be extracted in a Frame class
        JFrame frame = new JFrame("Moovite");
        frame.getContentPane().add(viewer);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public JXMapViewer getViewer() {
        return viewer;
    }

    public TileFactory getTileFactory() {
        return tileFactory;
    }

}
