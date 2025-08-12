package com.nnamo.controllers;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.nnamo.enums.Direction;
import com.nnamo.enums.RealtimeStatus;
import com.nnamo.enums.ResetType;
import com.nnamo.enums.UpdateMode;
import com.nnamo.interfaces.*;
import com.nnamo.models.*;
import com.nnamo.view.frame.MainFrame;
import com.nnamo.services.DatabaseService;
import com.nnamo.services.FeedUpdateListener;
import com.nnamo.services.RealtimeGtfsService;

import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class MainController {

    DatabaseService db;
    RealtimeGtfsService realtimeService;
    MainFrame mainFrame;
    UserController userController;
    UserModel sessionUser;
    boolean loaded = false;

    // CONSTRUCTORS //
    public MainController(DatabaseService db, RealtimeGtfsService realtimeService) throws IOException {
        this.db = db;
        this.userController = new UserController(db);
        this.realtimeService = realtimeService;
        this.mainFrame = new MainFrame();
    }

    // METHODS //
    public void run() throws InterruptedException, SQLException, IOException {

        mainFrame.renderStops(db.getAllStops());
        handleClickWaypointBehaviour();
        handleFavouriteButtonClicksBehaviour();
        handleTableBehaviour();
        handleButtonPanelClickBehaviour();
        mainFrame.getSearchPanel().addSearchListener(this::searchQueryListener);

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

        // Logout Behaviour.
        mainFrame.setLogoutBehaviour(new LogoutBehaviour() {
            @Override
            public void onLogout() {
                userController.deleteCurrentSession();
                mainFrame.close();
                try {
                    userController.run();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        handleRealtimeBehaviour();
        realtimeService.startBackgroundThread();
        mainFrame.setRealtimeStatus(RealtimeStatus.ONLINE); // Changing realtime status notifies the observer method,
                                                            // thus interacting with RealtimeService

        // Mostra tutte le fermate allo startup del programma.
        this.searchQueryListener("");
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

    private void updateStopPanel(StopModel stop, List<StopTimeModel> stopTimes,
            List<RealtimeStopUpdate> realtimeUpdates) throws SQLException, IOException {
        mainFrame.updateStopPanelInfo(stop.getId(), stop.getName());
        mainFrame.updateStopPanelTimes(stopTimes, realtimeUpdates);
        mainFrame.updateStopPanelFavButtons(db.isFavoriteStop(sessionUser.getId(), stop.getId()), stop.getId());
        mainFrame.updateStopPanelVisibility(true);
        mainFrame.updateStopPanelRoutes(db.getStopTimes(stop.getId()));
    }

    // BEHAVIOUR //
    private void handleRealtimeBehaviour() {
        // Listener for when Realtime Service changes status
        realtimeService.setRealtimeChangeListener(new RealtimeStatusChangeListener() {
            @Override
            public void onChange(RealtimeStatus newStatus) {
                mainFrame.setRealtimeStatus(newStatus);
                // TODO: notification for status change if going offline
            }
        });

        realtimeService.addListener(new FeedUpdateListener() {
            @Override
            public void onFeedUpdated(List<FeedEntity> entities) {
                try {

                    // Updates stop panel details
                    String stopId = mainFrame.getCurrentStopId();
                    if (stopId != null && !stopId.isEmpty()) {
                        List<StopTimeModel> stopTimes = db.getNextStopTimes(stopId, getCurrentTime(), getCurrentDate());
                        List<RealtimeStopUpdate> realtimeUpdates = realtimeService.getStopUpdatesById(stopId);
                        mainFrame.updateStopPanelTimes(stopTimes, realtimeUpdates);
                        System.out.println("Updated realtime details on feed update on stop " + stopId);
                    }

                    // Updates route vehicle positions
                    String routeId = mainFrame.getCurrentRouteId();
                    if (routeId != null && !routeId.isEmpty()) {
                        var positions = realtimeService.getRoutesVehiclePositions(routeId);
                        mainFrame.renderVehiclePositions(positions); // Update vehicle positions
                        System.out.println("Updated realtime vehicle positions on feed update on route " + routeId);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // Listener for switch button in MainFrame
        mainFrame.setRealtimeSwitchListener(new SwitchBarListener() {
            @Override
            public void onSwitch(RealtimeStatus newStatus) {
                realtimeService.setRealtimeStatus(newStatus);
            }
        });
    }

    private void handleFavouriteButtonClicksBehaviour() {

        // STOP BEHAVIOUR //
        FavoriteBehaviour favStopBehaviour = new FavoriteBehaviour() {
            @Override
            public void addFavorite(String stopId) {
                try {
                    db.addFavStop(sessionUser.getId(), stopId);
                    mainFrame.updateFavStopTable(db.getStopById(stopId), UpdateMode.ADD);
                    System.out.println(db.getFavoriteStops(sessionUser.getId()).stream().count());
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }

            @Override
            public void removeFavorite(String stopId) {
                try {
                    db.removeFavStop(sessionUser.getId(), stopId);
                    mainFrame.updateFavStopTable(db.getStopById(stopId), UpdateMode.REMOVE);
                    System.out.println(db.getFavoriteStops(sessionUser.getId()).stream().count());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        FavoriteBehaviour bothStopStopBehaviour = new FavoriteBehaviour() {
            @Override
            public void addFavorite(String string) {
                favStopBehaviour.addFavorite(string);
            }

            @Override
            public void removeFavorite(String string) {
                favStopBehaviour.removeFavorite(string);
                mainFrame.getLeftPanel().getPreferPanel().getRemoveStopButton().reset(ResetType.STOP);
            }
        };
        mainFrame.getStopPanel().getFavoriteStopButton().setFavBehaviour(bothStopStopBehaviour);

        FavoriteBehaviour onlyRemovePreferFavStopBehaviour = new FavoriteBehaviour() {
            @Override
            public void addFavorite(String string) {

            }

            @Override
            public void removeFavorite(String string) {
                favStopBehaviour.removeFavorite(string);
                mainFrame.getLeftPanel().getPreferPanel().getRemoveStopButton().reset();
                mainFrame.getStopPanel().getFavoriteStopButton().reset(ResetType.STOP);
            }
        };
        mainFrame.getLeftPanel().getPreferPanel().getRemoveStopButton()
                .setFavBehaviour(onlyRemovePreferFavStopBehaviour);

        // ROUTE BEHAVIOUR //
        FavoriteBehaviour favRouteBehaviour = new FavoriteBehaviour() {
            @Override
            public void addFavorite(String routeId) {
                try {
                    db.addFavRoute(sessionUser.getId(), routeId);
                    mainFrame.updateFavRouteTable(db.getRouteById(routeId), UpdateMode.ADD);
                    System.out.println(db.getFavoriteRoutes(sessionUser.getId()).stream().count());
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }

            @Override
            public void removeFavorite(String routeId) {
                try {
                    db.removeFavRoute(sessionUser.getId(), routeId);
                    mainFrame.updateFavRouteTable(db.getRouteById(routeId), UpdateMode.REMOVE);
                    System.out.println(db.getFavoriteRoutes(sessionUser.getId()).stream().count());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        mainFrame.getStopPanel().getFavoriteRouteButton().setFavBehaviour(favRouteBehaviour);

        FavoriteBehaviour onlyAddSearchFavRouteBehaviour = new FavoriteBehaviour() {

            @Override
            public void addFavorite(String string) {
                favRouteBehaviour.addFavorite(string);
                mainFrame.getLeftPanel().getSearchPanel().getAddRouteButton().reset();
            }

            @Override
            public void removeFavorite(String string) {

            }
        };
        mainFrame.getLeftPanel().getSearchPanel().getAddRouteButton().setFavBehaviour(onlyAddSearchFavRouteBehaviour);

        FavoriteBehaviour onlyRemovePreferFavRouteBehaviour = new FavoriteBehaviour() {

            @Override
            public void addFavorite(String string) {

            }

            @Override
            public void removeFavorite(String string) {
                favRouteBehaviour.removeFavorite(string);
                mainFrame.getLeftPanel().getSearchPanel().getAddRouteButton().reset();
                mainFrame.getStopPanel().getFavoriteRouteButton().reset(ResetType.GENERIC);
            }
        };
        mainFrame.getLeftPanel().getPreferPanel().getRemoveRouteButton()
                .setFavBehaviour(onlyRemovePreferFavRouteBehaviour);

        // FavoriteBehaviour bothStopFavRouteBehaviour = new FavoriteBehaviour() {
        // @Override
        // public void addFavorite(String string) {
        // favRouteBehaviour.addFavorite(string);
        // }
        //
        // @Override
        // public void removeFavorite(String string) {
        // favRouteBehaviour.removeFavorite(string);
        // }
        // };
    }

    private void handleTableBehaviour() {

        TableCheckIsFavBehaviour checkIsFavBehaviour = new TableCheckIsFavBehaviour() {
            @Override
            public boolean onCheckIsFav(Vector<Object> rowData, int columnIndex) {
                try {
                    String itemId = (String) (rowData).get(columnIndex);
                    return db.isFavoriteStop(sessionUser.getId(), itemId)
                            || db.isFavouriteRoute(sessionUser.getId(), itemId);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
        mainFrame.setTableCheckIsFavBehaviour(checkIsFavBehaviour);

        // STOP TABLE BEHAVIOUR //
        TableRowClickBehaviour zoomStopClickBehaviour = new TableRowClickBehaviour() {
            @Override
            public void onRowClick(Object rowData, int columnIndex, boolean isFav) throws SQLException, IOException {
                final int zoomLevel = 0;
                String stopId = (String) ((List<Object>) rowData).get(columnIndex);
                StopModel stop = db.getStopById(stopId);
                GeoPosition geoPosition = new GeoPosition(stop.getLatitude(), stop.getLongitude());
                mainFrame.setMapPanelMapPosition(geoPosition, zoomLevel);
                updateStopPanel(stop, db.getNextStopTimes(stopId, getCurrentTime(), getCurrentDate()),
                        realtimeService.getStopUpdatesById(stopId));
                List<RealtimeStopUpdate> realtimeUpdates = realtimeService.getStopUpdatesById(stop.getId());

                try {
                    updateStopPanel(stop, db.getNextStopTimes(stopId, getCurrentTime(), getCurrentDate()),
                            realtimeUpdates);
                    mainFrame.updateStopPanelFavButtons(isFav, stopId);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        mainFrame.setSearchStopRowClickBehaviour(zoomStopClickBehaviour);

        TableRowClickBehaviour PreferStopClickBehaviour = new TableRowClickBehaviour() {
            @Override
            public void onRowClick(Object rowData, int columnIndex, boolean isFav) throws SQLException, IOException {
                zoomStopClickBehaviour.onRowClick(rowData, columnIndex, isFav);
                String stopId = (String) ((List<Object>) rowData).get(columnIndex);
                mainFrame.getLeftPanel().getPreferPanel().getRemoveStopButton().setItemId(stopId);
                mainFrame.getLeftPanel().getPreferPanel().getRemoveStopButton().update(isFav);

                mainFrame.getStopPanel().getFavoriteStopButton().setItemId(stopId);
                mainFrame.getStopPanel().getFavoriteStopButton().update(isFav);
                System.out.println(
                        "Click su Prefer Panel Stop: " + stopId + " is favorite: " + isFav + "XXXXXXXXXXXXXXXX");
            }
        };
        mainFrame.getLeftPanel().getPreferPanel().setFavStopRowClickBehaviour(PreferStopClickBehaviour);

        // ROUTE TABLE BEHAVIOUR //
        TableRowClickBehaviour zoomRouteClickBehaviour = new TableRowClickBehaviour() {
            @Override
            public void onRowClick(Object rowData, int columnIndex, boolean isFav) throws SQLException {
                String routeId = (String) ((List<Object>) rowData).get(columnIndex);

                // get model from the db
                List<StopModel> stopModels = db.getOrderedStopsForRoute(routeId, Direction.OUTBOUND);
                if (stopModels.isEmpty()) {
                    System.out.println("No stops found for route: " + routeId);
                    return;
                }

                // get the first stop to center the map
                StopModel firstStop = stopModels.get(columnIndex);

                GeoPosition geoPosition = new GeoPosition(firstStop.getLatitude(), firstStop.getLongitude());
                int zoomLevel = 5; // default zoom level

                List<VehiclePosition> routePositions = realtimeService.getRoutesVehiclePositions(routeId);

                // render stops and route lines on the map
                mainFrame.renderRouteLines(stopModels, routePositions, routeId, geoPosition, zoomLevel);
            }
        };
        TableRowClickBehaviour noZoomRouteClickBehaviour = new TableRowClickBehaviour() {
            @Override
            public void onRowClick(Object rowData, int columnIndex, boolean isFav) throws SQLException {
                String routeId = (String) ((List<Object>) rowData).get(columnIndex);
                mainFrame.setCurrentRouteId(routeId);
            }
        };

        TableRowClickBehaviour StopInfoRouteClickBehaviour = new TableRowClickBehaviour() {
            @Override
            public void onRowClick(Object rowData, int columnIndex, boolean isFav) throws SQLException, IOException {
                zoomRouteClickBehaviour.onRowClick(rowData, columnIndex, isFav);
                String routeId = (String) ((List<Object>) rowData).get(columnIndex);

                mainFrame.getStopPanel().getFavoriteRouteButton().update(isFav);
                mainFrame.getStopPanel().getFavoriteRouteButton().setItemId(routeId);

                List<StopModel> stopModels = db.getOrderedStopsForRoute(routeId, Direction.OUTBOUND);
                StopModel firstStop = stopModels.getFirst();
                GeoPosition geoPosition = new GeoPosition(firstStop.getLatitude(), firstStop.getLongitude());

                List<VehiclePosition> routePositions = realtimeService.getRoutesVehiclePositions(routeId);

                mainFrame.renderRouteLines(stopModels, routePositions, routeId, geoPosition, 1);
            }
        };
        mainFrame.setStopInfoRowClickBehaviour(StopInfoRouteClickBehaviour);

        TableRowClickBehaviour StopTimeRouteClickBehaviour = new TableRowClickBehaviour() {
            @Override
            public void onRowClick(Object rowData, int columnIndex, boolean isFav) throws SQLException, IOException {
                noZoomRouteClickBehaviour.onRowClick(rowData, columnIndex, isFav);
                String routeId = (String) ((List<Object>) rowData).get(columnIndex);
            }
        };
        mainFrame.setStopRouteRowClickBehaviour(StopTimeRouteClickBehaviour);

        TableRowClickBehaviour PreferRouteClickBehaviour = new TableRowClickBehaviour() {
            @Override
            public void onRowClick(Object rowData, int columnIndex, boolean isFav) throws SQLException, IOException {
                zoomRouteClickBehaviour.onRowClick(rowData, columnIndex, isFav);

                String routeId = (String) ((List<Object>) rowData).get(columnIndex);

                mainFrame.getLeftPanel().getPreferPanel().getRemoveRouteButton().setItemId((routeId));
                mainFrame.getLeftPanel().getPreferPanel().getRemoveRouteButton().update(isFav);
                System.out.println(
                        "Click su Prefer Panel Route: " + routeId + " is favorite: " + isFav + "XXXXXXXXXXXXXXXX");
            }
        };
        mainFrame.setFavRouteRowClickBehaviour(PreferRouteClickBehaviour);

        TableRowClickBehaviour SearchRouteClickBehaviour = new TableRowClickBehaviour() {
            @Override
            public void onRowClick(Object rowData, int columnIndex, boolean isFav) throws SQLException, IOException {
                zoomRouteClickBehaviour.onRowClick(rowData, columnIndex, isFav);

                String routeId = (String) ((List<Object>) rowData).get(columnIndex);
                boolean isFavorite = db.isFavouriteRoute(sessionUser.getId(), routeId);

                mainFrame.getLeftPanel().getSearchPanel().getAddRouteButton().update(isFavorite);
                mainFrame.getLeftPanel().getSearchPanel().getAddRouteButton()
                        .setItemId((String) ((List<Object>) rowData).get(columnIndex));
                System.out.println(
                        "Click su Search Panel Route: " + routeId + " is favorite: " + isFavorite + "XXXXXXXXXXXXXXXX");
            }
        };
        mainFrame.setSearchRouteRowClickBehaviour(SearchRouteClickBehaviour);
    }

    private void handleClickWaypointBehaviour() {

        WaypointBehaviour clickWaypointBehaviour = new WaypointBehaviour() {
            @Override
            public void onWaypointClick(GeoPosition geo, boolean b) throws SQLException, IOException {
                // Convert the GeoPosition of the click to pixel coordinates
                // then get the current icon from the StopPainter
                if (geo != null) {
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
                        if (clickXIcon >= 0 && clickXIcon < iconWidth && clickYIcon >= 0
                                && clickYIcon < iconImgHeight) {
                            // Get the pixel color at the click position
                            int argb = currentIcon.getRGB(clickXIcon, clickYIcon);
                            // Create a Color object to check the alpha(transparency) value
                            int alpha = new Color(argb, true).getAlpha();
                            // Check alpha
                            if (alpha > 0) {
                                List<StopTimeModel> stopTimes = db.getNextStopTimes(stop.getId(), getCurrentTime(),
                                        getCurrentDate());
                                List<RealtimeStopUpdate> realtimeUpdates = realtimeService
                                        .getStopUpdatesById(stop.getId());
                                updateStopPanel(stop, stopTimes, realtimeUpdates);
                                return;
                            }
                        }
                    }

                    mainFrame.updateStopPanelVisibility(false);
                    mainFrame.getMapPanel().repaintView();
                } else {
                    mainFrame.updateStopPanelVisibility(false);
                }
            }
        };
        mainFrame.setClickWaypointBehaviour(clickWaypointBehaviour);
    }

    private void handleButtonPanelClickBehaviour() {

        ButtonPanelBehaviour baseButtonPanelBehaviour = new ButtonPanelBehaviour() {
            @Override
            public void onButtonPanelClick(JPanel panel) {
                if (panel.isVisible()) {
                    mainFrame.updateLeftPanelVisibility(false);
                    mainFrame.updateLeftPanelModularPanel(panel, false);
                } else {
                    mainFrame.updateLeftPanelVisibility(true);
                    mainFrame.updateLeftPanelModularPanel(panel, true);
                }
            }
        };
        mainFrame.setButtonPanelGeneralBehaviour(baseButtonPanelBehaviour);

        ButtonPanelBehaviour preferButtonPanelBehaviour = new ButtonPanelBehaviour() {
            @Override
            public void onButtonPanelClick(JPanel panel) {
                if (!loaded) {
                    try {
                        mainFrame.initLeftPanelPreferPanelPreferTable(
                                db.getFavoriteStops(sessionUser.getId()),
                                db.getFavoriteRoutes(sessionUser.getId()));
                        loaded = true;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                baseButtonPanelBehaviour.onButtonPanelClick(panel); // TODO: not sure, but maybe need to refactor this
            };
        };
        mainFrame.setButtonPanelPreferBehaviour(preferButtonPanelBehaviour);
    }

    public void searchQueryListener(String searchText) {
        var searchPanel = mainFrame.getSearchPanel();
        try {
            var stops = db.getStopsByName(searchText);
            var routes = db.getRoutesByName(searchText);
            searchPanel.updateView(stops, routes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
