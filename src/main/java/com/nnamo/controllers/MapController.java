package com.nnamo.controllers;

import com.nnamo.interfaces.WaypointListener;
import com.nnamo.models.StopModel;
import com.nnamo.view.MapView;
import com.nnamo.services.DatabaseService;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.GeoPosition;
import org.onebusaway.gtfs.model.Stop;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class MapController {

    DatabaseService db;
    MapView mapView = new MapView();

    public MapController(DatabaseService db) {
        this.db = db;
    }

    public void run() throws SQLException {
        mapView.renderStops(db.getAllStops());
        mapView.setWaypointListener(new WaypointListener() {
            @Override
            public void waypointClicked(GeoPosition geo) throws SQLException, IOException {
                Point2D clickPixel = mapView.getViewer().convertGeoPositionToPoint(geo);
                BufferedImage iconImg = ImageIO.read(Objects.requireNonNull(DefaultWaypointRenderer.class.getResource("/images/waypoint_white.png")));

                for (StopModel stop : db.getAllStops()) {
                    GeoPosition stopGeo = new GeoPosition(stop.getLatitude(), stop.getLongitude());
                    Point2D stopPixel = mapView.getViewer().convertGeoPositionToPoint(stopGeo);

                    int iconW = iconImg.getWidth();
                    int iconH = iconImg.getHeight();
                    int anchorX = iconW / 2;
                    int anchorY = iconH;
                    int dx = (int) (clickPixel.getX() - (stopPixel.getX() - anchorX));
                    int dy = (int) (clickPixel.getY() - (stopPixel.getY() - iconH));

                    if (dx >= 0 && dx < iconW && dy >= 0 && dy < iconH) {
                        int argb = iconImg.getRGB(dx, dy);
                        int alpha = (argb >> 24) & 0xff;
                        if (alpha > 0) {
                            System.out.println("Click su pixel visibile!");
                            JFrame f = new JFrame("Stop Details");
                            f.setSize(400, 300);
                            f.setVisible(true);
                        }
                    }
                }
            }
        });
        mapView.run();
    }
}