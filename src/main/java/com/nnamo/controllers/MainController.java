package com.nnamo.controllers;

import com.nnamo.interfaces.*;
import com.nnamo.models.StopModel;
import com.nnamo.models.StopTimeModel;
import com.nnamo.models.UserModel;
import com.nnamo.view.frame.MainFrame;
import com.nnamo.services.DatabaseService;
import com.nnamo.services.RealtimeGtfsService;
import com.nnamo.services.RealtimeStopUpdate;

import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public class MainController {

    DatabaseService db;
    RealtimeGtfsService realtimeService;
    MainFrame mainFrame;
    UserController userController;
    UserModel sessionUser;

    public MainController(DatabaseService db, RealtimeGtfsService realtimeService) throws IOException {
        this.db = db;
        this.userController = new UserController(db);
        this.realtimeService = realtimeService;
        this.mainFrame = new MainFrame();
    }

    public void run() throws InterruptedException, SQLException, IOException {

        // Initialize main frame
        mainFrame.renderStops(db.getAllStops());
        handleStopClick();
        handleFavouriteButtonClicks();
        handleSearchPanelTableRowClick();
        mainFrame.getSearchPanel().addSearchListener(this::searchQueryListener);
        ;

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

        // Questo triggera il listener della barra di ricerca per mostrare tutte le
        // fermate
        // allo startup del programma.
        this.searchQueryListener("");
    }

    private void handleFavouriteButtonClicks() {
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

        mainFrame.setStopTimeTableClickListener(new TableRowClickListener() {
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
    }

    private void handleStopClick() {
        mainFrame.getMapPanel().setWaypointListener(new WaypointListener() {
            @Override
            public void onWaypointClick(GeoPosition geo) throws SQLException, IOException {
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
                            List<StopTimeModel> stopTimes = db.getNextStopTimes(stop.getId(), getCurrentTime(),
                                    getCurrentDate());
                            List<RealtimeStopUpdate> realtimeUpdates = realtimeService.getStopUpdatesById(stop.getId());
                            updateStopPanel(stop, stopTimes, realtimeUpdates);
                            return;
                        }
                    }
                }

                mainFrame.updateStopPanelVisibility(false);
                mainFrame.getMapPanel().repaint();
            }
        });
    }

    private void handleSearchPanelTableRowClick() {
        mainFrame.setSearchStopTableClickListener(new TableRowClickListener() {
            @Override
            public void onRowClick(Object rowData) throws SQLException {
                final int zoomLevel = 0;
                String stopId = (String) ((List<Object>) rowData).get(1);
                StopModel stop = db.getStopById(stopId);
                GeoPosition geoPosition = new GeoPosition(stop.getLatitude(), stop.getLongitude());
                mainFrame.setMapPanelMapPosition(geoPosition, zoomLevel);
                List<RealtimeStopUpdate> realtimeUpdates = realtimeService.getStopUpdatesById(stop.getId());
                try {
                    updateStopPanel(stop, db.getNextStopTimes(stopId, getCurrentTime(), getCurrentDate()),
                            realtimeUpdates);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        mainFrame.setSearchRouteTableClickListener(new TableRowClickListener() {
            @Override
            public void onRowClick(Object rowData) throws SQLException {
                String routeId = (String) ((List<Object>) rowData).get(1);
                System.out.println("Route clicked: " + routeId);

                // get model from the db
                List<StopModel> stopModels = db.getOrderedStopsForRoute(routeId);
                if (stopModels.isEmpty()) {
                    System.out.println("No stops found for route: " + routeId);
                    return;
                }

                // get the first stop to center the map
                StopModel firstStop = stopModels.get(0);

                // debug the positions
                for (StopModel stop : stopModels) {
                    System.out.println("Stop: " + stop.getName() + " - Position: "
                            + stop.getLatitude() + ", " + stop.getLongitude());
                }

                GeoPosition geoPosition = new GeoPosition(firstStop.getLatitude(), firstStop.getLongitude());
                int zoomLevel = 0; // default zoom level
                mainFrame.setMapPanelMapPosition(geoPosition, zoomLevel);

                // render stops and route lines on the map
                mainFrame.getMapPanel().renderStopsRoute(stopModels);
                mainFrame.getMapPanel().repaint();
            }
        });

    }

    private void updateStopPanel(StopModel stop, List<StopTimeModel> stopTimes,
            List<RealtimeStopUpdate> realtimeUpdates)
            throws SQLException, IOException {
        mainFrame.updateStopPanelInfo(stop.getId(), stop.getName());
        mainFrame.updateStopPanelTimes(stopTimes, realtimeUpdates);
        mainFrame.updateStopPanelPreferButtons(db.isFavoriteStop(sessionUser.getId(), stop.getId()), stop.getId());
        mainFrame.updateStopPanelVisibility(true);
    }

    public void searchQueryListener(String searchText) {
        // if (searchText == null || searchText.isEmpty()) {
        // mainFrame.getSearchPanel().updateView(new ArrayList<>());
        // return; // Exit if the search text is empty
        // }

        var searchPanel = mainFrame.getSearchPanel();
        try {
            var stops = db.getStopsByName(searchText);
            var routes = db.getRoutesByName(searchText);
            searchPanel.updateView(stops, routes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Date getCurrentDate() {
        return new Date();
    }

    public LocalTime getCurrentTime() {
        return LocalTime.now();
    }

    public void setLocalMapCache(File cacheDir) {
        mainFrame.setLocalMapCache(cacheDir);
    }
}
