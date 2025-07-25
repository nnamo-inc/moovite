package com.nnamo.controllers;

import com.nnamo.interfaces.FavoriteStopBehaviour;
import com.nnamo.interfaces.SessionListener;
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

public class MainController {

    DatabaseService db;
    MainFrame mainFrame;
    UserController userController;
    UserModel sessionUser;

    public MainController(DatabaseService db) throws IOException {
        this.db = db;
        this.userController = new UserController(db);
    }

    public void run() throws InterruptedException, SQLException, IOException {
        System.out.println("MainController started");

        // Initialize main frame
        mainFrame = new MainFrame();
        mainFrame.getMapPanel().renderStops(db.getAllStops());
        handleStopClick();
        handleFavouriteClicks();

        // Login and Session Fetching
        userController.addSessionListener(new SessionListener() { // [!] Listener must be implemented before run()
            @Override
            public void onSessionCreated(int userId) {
                try {
                    sessionUser = db.getUserById(userId);
                    mainFrame.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });
        userController.run();
    }

    private void handleFavouriteClicks() {
        mainFrame.setFavStopBehaviour(new FavoriteStopBehaviour() {
            @Override
            public void addFavoriteStop(String stopId) {
                try {
                    db.addFavoriteStop(sessionUser.getId(), stopId);
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });
    }

    private void handleStopClick() {
        mainFrame.getMapPanel().setWaypointListener(new WaypointListener() {
            @Override
            public void waypointClicked(GeoPosition geo) throws SQLException, IOException {
                // Convert the GeoPosition of the click to pixel coordinates
                // then get the current icon from the StopPainter
                Point2D clickPixel = mainFrame.getMapPanel().getMap().convertGeoPositionToPoint(geo);
                BufferedImage currentIcon = mainFrame.getCurrentStopIcon();
                if (currentIcon == null) {
                    return;
                }
                // For each stop in the database, create and then convert the GeoPosition to
                // pixel coordinates
                // then check if the click position is inside the icon bounds
                for (StopModel stop : db.getAllStops()) {
                    GeoPosition stopGeo = new GeoPosition(stop.getLatitude(), stop.getLongitude());
                    Point2D stopPixel = mainFrame.getMapPanel().getMap().convertGeoPositionToPoint(stopGeo);
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
                            var stopTimes = db.getNextStopTimes(stop.getId(), LocalTime.now());
                            updateStopPanel(stop, stopTimes);
                            mainFrame.getStopPanel().revalidate();
                            mainFrame.getStopPanel().setVisible(true);
                            return;
                        }
                    }
                }
                mainFrame.getStopPanel().setVisible((false));
                mainFrame.getMapPanel().repaint();
            }
        });
    }

    private void updateStopPanel(StopModel stop, List<StopTimeModel> stopTimes) throws SQLException, IOException {
        mainFrame.setStopId(stop.getId());
        mainFrame.setStopName(stop.getName());
        mainFrame.updateStopTimes(stopTimes);
    }
}
