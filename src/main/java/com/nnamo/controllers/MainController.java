package com.nnamo.controllers;

import com.nnamo.interfaces.*;
import com.nnamo.models.RouteModel;
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
import java.util.Vector;

public class MainController {

    DatabaseService db;
    MainFrame mainFrame = new MainFrame();
    UserController userController;
    UserModel sessionUser;

    public MainController(DatabaseService db) throws IOException {
        this.db = db;
        this.userController = new UserController(db);
    }

    public void run() throws InterruptedException, SQLException, IOException {
        System.out.println("MainController started");

        // Initialize main frame
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
        mainFrame.setFavStopBehaviour(new FavoriteBehaviour() {

            @Override
            public void addFavorite(String stopId) {
                try {
                    db.addFavoriteStop(sessionUser.getId(), stopId);
                    System.out.println(db.getFavoriteStops(sessionUser.getId()).stream().count());
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }

            @Override
            public void removeFavorite(String stopId) {
                try {
                    db.removeFavoriteStop(sessionUser.getId(), stopId);
                    System.out.println(db.getFavoriteStops(sessionUser.getId()).stream().count());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        mainFrame.setTableClickListener(new TableClickListener() {
            @Override
            public void onRowClick(Object rowData) throws SQLException {
                String routeNumber = (String) ((List<Object>) rowData).get(0);
                boolean isFavorite = db.isFavouriteRoute(sessionUser.getId(), routeNumber);
                mainFrame.updatePreferRouteButton(isFavorite, routeNumber);
            }
        });

        mainFrame.setFavLineBehaviour(new FavoriteBehaviour() {
            @Override
            public void addFavorite(String string) {
                try {
                    db.addFavoriteRoute(sessionUser.getId(), string);
                    System.out.println(db.getFavoriteRoutes(sessionUser.getId()).stream().count());
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }

            @Override
            public void removeFavorite(String string) {
                try {
                    db.removeFavoriteRoute(sessionUser.getId(), string);
                    System.out.println(db.getFavoriteRoutes(sessionUser.getId()).stream().count());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        /*
         * mainFrame.setFavLineBehaviour(new FavoriteLineBehaviour() {
         * 
         * @Override
         * public void addFavoriteLine(String lineId) {
         * try {
         * db.addFavoriteRoute();
         * mainFrame.updateStopPanelPreferRouteButton("Add route to favourite");
         * } catch (SQLException e) {
         * e.printStackTrace();
         * System.exit(1);
         * }
         * }
         * });
         */
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
                            List<StopTimeModel> stopTimes = db.getNextStopTimes(stop.getId(), LocalTime.now());
                            openAndUpdateStopPanel(stop, stopTimes);
                            return;
                        }
                    }
                }
                mainFrame.getStopPanel().setVisible((false));
                mainFrame.getMapPanel().repaint();
            }
        });
    }

    private void openAndUpdateStopPanel(StopModel stop, List<StopTimeModel> stopTimes)
            throws SQLException, IOException {
        mainFrame.updateStopPanelInfo(stop.getId(), stop.getName());
        mainFrame.updateStopPanelTimes(stopTimes);
        mainFrame.updateStopPanelPreferButtons(db.isFavoriteStop(sessionUser.getId(), stop.getId()), stop.getId());
        mainFrame.getStopPanel().revalidate();
        mainFrame.getStopPanel().setVisible(true);
        mainFrame.getStopPanel().setVisible(true);
    }

    // TODO implements update prefer route button text
    /*
     * private void updateStopPanelPreferRouteButton() throws SQLException {
     * if (mainFrame.isRouteButtonEnabled() &&
     * db.isFavouriteRoute(sessionUser.getId(), route.getId())) {
     * mainFrame.updateStopPanelPreferRouteButton("Rimuovi percorso dai preferiti");
     * } else {
     * mainFrame.updateStopPanelPreferRouteButton("Aggiungi percorso ai preferiti");
     * }
     * }
     */
}
