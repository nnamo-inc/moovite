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

    // reference to the DatabaseService (models)
    DatabaseService db;
    // reference to the MapView (view)
    MapView mapView = new MapView();

    // CONSTRUCTOR //
    public MapController(DatabaseService db) throws IOException {
        this.db = db;
    }

    // METHODS //
    public void run() throws SQLException, IOException {
        System.out.println("MapController started");
        // Create all the stops waypoints, then set them to the waypointPainter
        mapView.getMapPanel().renderStops(db.getAllStops());
        // Set the listener for the waypoint clicks with an anonymous inner class
        mapView.getMapPanel().setWaypointListener(new WaypointListener() {
            @Override
            public void waypointClicked(GeoPosition geo) throws SQLException, IOException {
                // Convert the GeoPosition of the click to pixel coordinates
                // then get the current icon from the StopPainter
                Point2D clickPixel = mapView.getMapPanel().getMap().convertGeoPositionToPoint(geo);
                BufferedImage currentIcon = mapView.getMapPanel().getStopPainter().getCurrentIcon();
                if (currentIcon == null) {
                    return;
                }
                // For each stop in the database, create and then convert the GeoPosition to pixel coordinates
                // then check if the click position is inside the icon bounds
                for (StopModel stop : db.getAllStops()) {
                    GeoPosition stopGeo = new GeoPosition(stop.getLatitude(), stop.getLongitude());
                    Point2D stopPixel = mapView.getMapPanel().getMap().convertGeoPositionToPoint(stopGeo);
                    // Get icon width and height
                    int iconWidth = currentIcon.getWidth();
                    int iconImgHeight = currentIcon.getHeight();
                    // Get icon pixel pointer position
                    int iconPointerWidth = iconWidth / 2;
                    int iconPointerHeight = iconImgHeight;
                    // Calculate the click position relative to the icon
                    int clickXIcon = (int) (clickPixel.getX() - (stopPixel.getX() - iconPointerWidth));
                    int clickYIcon = (int) (clickPixel.getY() - (stopPixel.getY() - iconImgHeight));
                    // Check if the click is inside the icon bounds and find witch stop was clicked
                    if (clickXIcon >= 0 && clickXIcon < iconWidth && clickYIcon >= 0 && clickYIcon < iconImgHeight) {
                        // Get the pixel color at the click position
                        int argb = currentIcon.getRGB(clickXIcon, clickYIcon);
                        // Create a Color object to check the alpha(transparency) value
                        int alpha = new Color(argb, true).getAlpha();
                        // Check alpha
                        if (alpha > 0) {
                            System.out.println("Click su fermata!");
                            StopPanelManager(stop);
                            mapView.getStopPanel().revalidate();
                            mapView.getStopPanel().setVisible(true);
                            return;
                        }
                    }
                }
                mapView.getStopPanel().setVisible((false));
                mapView.getMapPanel().repaint();
            }
        });
    }

    private void StopPanelManager(StopModel stop) throws SQLException, IOException {
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        // HERE WE'LL GET ALL THE INFO FROM THE DATABASE AND SET IT TO THE STOP PANEL
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        mapView.getStopPanel().getTextID().setText(stop.getId()); // Get and modify the stop ID
        mapView.getStopPanel().getTextName().setText(stop.getName()); // Get and modify the stop name
    }
}
