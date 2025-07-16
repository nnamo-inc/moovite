package com.nnamo.controllers;

import com.nnamo.interfaces.WaypointListener;
import com.nnamo.models.StopModel;
import com.nnamo.view.MapView;
import com.nnamo.services.DatabaseService;
import org.jxmapviewer.viewer.GeoPosition;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

public class MapController {

    DatabaseService db;
    MapView mapView = new MapView();

    public MapController(DatabaseService db) throws IOException {
        this.db = db;
    }

    public void run() throws SQLException, IOException {
        mapView.getMapPanel().renderStops(db.getAllStops());
        mapView.getMapPanel().setWaypointListener(new WaypointListener() {
            @Override
            public void waypointClicked(GeoPosition geo) throws SQLException, IOException {
                Point2D clickPixel = mapView.getMapPanel().getMap().convertGeoPositionToPoint(geo); // convert click GeoPosition to pixel
                BufferedImage currentIcon = mapView.getMapPanel().getStopPainter().getCurrentIcon();

                if (currentIcon == null) {
                    return;
                }

                for (StopModel stop : db.getAllStops()) {
                    GeoPosition stopGeo = new GeoPosition(stop.getLatitude(), stop.getLongitude()); // lat and lon of the stop
                    Point2D stopPixel = mapView.getMapPanel().getMap().convertGeoPositionToPoint(stopGeo); // convert stop
                                                                                                // GeoPosition to pixel

                    // get icon dimensions
                    int iconWidth = currentIcon.getWidth();
                    int iconImgHeight = currentIcon.getHeight();
                    // get icon relative pointer position
                    int iconPointerWidth = iconWidth / 2;
                    int iconPointerHeight = iconImgHeight;
                    // calculate the click position relative to the icon
                    int clickX = (int) (clickPixel.getX() - (stopPixel.getX() - iconPointerWidth));
                    int clickY = (int) (clickPixel.getY() - (stopPixel.getY() - iconImgHeight));
                    // check if the click is inside the icon bounds
                    if (clickX >= 0 && clickX < iconWidth && clickY >= 0 && clickY < iconImgHeight) {
                        // get the pixel
                        int argb = currentIcon.getRGB(clickX, clickY);
                        // create a Color object to check the alpha(transparency) value
                        int alpha = new Color(argb, true).getAlpha();
                        // check alpha
                        if (alpha > 0) {
                            System.out.println("Click su pixel visibile!");
                            JFrame f = new JFrame("Stop Details");
                            f.setSize(400, 300);
                            f.setVisible(true);
                            break;
                        }
                    }
                }
            }
        });

        //mapView.run();
        System.out.println("In");
        mapView.setVisible(true);
        System.out.println("out");
    }
}
