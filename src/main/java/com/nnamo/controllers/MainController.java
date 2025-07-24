package com.nnamo.controllers;

import com.nnamo.interfaces.WaypointListener;
import com.nnamo.models.StopModel;
import com.nnamo.models.StopTimeModel;
import com.nnamo.models.UserModel;
import com.nnamo.view.frame.MainFrame;
import com.nnamo.services.DatabaseService;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MainController {

    DatabaseService db;
    MainFrame mapView = new MainFrame();
    UserController userController;
    UserModel sessionUser;

    public MainController(DatabaseService db) throws IOException {
        this.db = db;
        this.userController = new UserController(db);
    }

    public void run() throws InterruptedException, SQLException, IOException {
        System.out.println("MapController started");

        // Login and Session Fetching
        userController.run();
        // TODO: add locking. It should wait until
        userController.waitForLogin();
        this.sessionUser = db.getUserById(userController.getCurrentUserId());

        mapView.getMapPanel().renderStops(db.getAllStops());
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
                // For each stop in the database, create and then convert the GeoPosition to
                // pixel coordinates
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

                            var stopTimes = db.getNextStopTimes(stop.getId(), LocalTime.now());
                            updateStopPanel(stop, stopTimes);
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

    private void updateStopPanel(StopModel stop, List<StopTimeModel> stopTimes) throws SQLException, IOException {
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        // HERE WE'LL GET ALL THE INFO FROM THE DATABASE AND SET IT TO THE STOP PANEL
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
        mapView.getStopPanel().getTextID().setText(stop.getId()); // Get and modify the stop ID
        mapView.getStopPanel().getTextName().setText(stop.getName()); // Get and modify the stop name
        mapView.updateStopTimes(stopTimes);
    }
}
