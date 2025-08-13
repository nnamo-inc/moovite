package com.nnamo.controllers;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.nnamo.enums.*;
import com.nnamo.interfaces.*;
import com.nnamo.models.*;
import com.nnamo.view.customcomponents.*;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.nnamo.enums.DataType.*;

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
        mainFrame.getLeftPanel().getStatisticsPanel().setupListeners(realtimeService);

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
        mainFrame.updateStopPanelVisibility(true);
        mainFrame.updateStopPanelRoutes(db.getStopTimes(stop.getId()));
    }

    private void updatePreferButton(String itemId, boolean isFav, DataType dataType) {
        mainFrame.updatePreferButton(itemId, isFav, dataType);
    }

    private GeoPosition calculateBaricentro(List<StopModel> stopList) {
        double latStop = 0;
        double lonStop = 0;
        int divider = 0;

        for (StopModel stop : stopList) {
            divider++;
            latStop += stop.getLatitude();
            lonStop += stop.getLongitude();
        }
        return new GeoPosition(latStop/divider, lonStop/divider);
    }

    private int calculateZoomLevel(List<StopModel> stopList) {
        int zoomLevel;
        double topPosition = Double.MIN_VALUE;
        double bottomPosition = Double.MAX_VALUE;
        double leftPosition = Double.MAX_VALUE;
        double rightPosition = Double.MIN_VALUE;

        for (StopModel stop : stopList) {
            topPosition = Math.max(topPosition, stop.getLatitude());
            bottomPosition = Math.min(bottomPosition, stop.getLatitude());
            leftPosition = Math.min(leftPosition, stop.getLongitude());
            rightPosition = Math.max(rightPosition, stop.getLongitude());
        }

        double latDiff = topPosition - bottomPosition;
        double lonDiff = rightPosition - leftPosition;
        double absoluteDiff = Math.max(latDiff, lonDiff);

        if (absoluteDiff > 0.12) {
            zoomLevel = 7; // zoom molto lontano per route molto estese
        } else if (absoluteDiff > 0.050) {
            zoomLevel = 6; // zoom molto lontano per route molto estese
        } else if (absoluteDiff > 0.030) {
            zoomLevel = 5; // zoom intermedio
        } else if (absoluteDiff > 0.013) {
            zoomLevel = 4; // zoom più vicino per route medie
        } else if (absoluteDiff > 0.010) {
            zoomLevel = 3; // zoom più vicino per route piccole
        } else if (absoluteDiff > 0.009) {
            zoomLevel = 2; // zoom molto vicino per route piccole
        } else {
            zoomLevel = 1; // zoom molto vicino per route piccolissime
        }
        return zoomLevel;
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
        FavoriteBehaviour generalFavBehaviour = new FavoriteBehaviour() {
            @Override
            public void addFavorite(String itemId, DataType dataType) {
                try {
                    switch (dataType) {
                        case STOP: {
                            db.addFavStop(sessionUser.getId(), itemId);
                            mainFrame.updateFavStopTable(db.getStopById(itemId), UpdateMode.ADD);
                            mainFrame.updatePreferButton(itemId, true, DataType.STOP);
                            System.out.println(db.getFavoriteStops(sessionUser.getId()).stream().count());
                            break;
                        }
                        case ROUTE: {
                            db.addFavRoute(sessionUser.getId(), itemId);
                            mainFrame.updateFavRouteTable(db.getRouteById(itemId), UpdateMode.ADD);
                            mainFrame.updatePreferButton(itemId, true, DataType.ROUTE);
                            System.out.println(db.getFavoriteRoutes(sessionUser.getId()).stream().count());
                            break;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }

            @Override
            public void removeFavorite(String itemId, DataType dataType) {
                try {
                    switch (dataType) {
                        case STOP: {
                            db.removeFavStop(sessionUser.getId(), itemId);
                            mainFrame.updateFavStopTable(db.getStopById(itemId), UpdateMode.REMOVE);
                            mainFrame.updatePreferButton(itemId, false, dataType);
                            System.out.println(db.getFavoriteStops(sessionUser.getId()).stream().count());
                            break;
                        }
                        case ROUTE: {
                            db.removeFavRoute(sessionUser.getId(), itemId);
                            mainFrame.updateFavRouteTable(db.getRouteById(itemId), UpdateMode.REMOVE);
                            mainFrame.updatePreferButton(itemId, false, dataType);
                            System.out.println(db.getFavoriteRoutes(sessionUser.getId()).stream().count());
                            break;
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        mainFrame.setGeneralFavBehaviour(generalFavBehaviour);
    }

    private void handleTableBehaviour() {

        TableRowClickBehaviour genericTableRowClickBehaviour = new TableRowClickBehaviour() {
            @Override
            public void onRowClick(Object rowData, ColumnName[] columnNames, DataType dataType)
                    throws SQLException, IOException {
                List<ColumnName> columnsList = Arrays.asList(columnNames);
                String itemId = (String) ((List<Object>) rowData).get(
                        columnsList.indexOf(ColumnName.CODICE));
                boolean isFav = switch (dataType) {
                    case STOP -> {
                        StopModel stop = db.getStopById(itemId);
                        GeoPosition geoPosition = new GeoPosition(stop.getLatitude(), stop.getLongitude());
                        mainFrame.setMapPanelMapPosition(geoPosition, 1);
                        yield db.isFavoriteStop(sessionUser.getId(), itemId);
                    }
                    case ROUTE -> {
                        Direction direction = Direction.OUTBOUND;
                        int directionIndex = columnsList.indexOf(ColumnName.DIREZIONE);
                        if (directionIndex != -1) {
                            String directionName = (String) ((List<Object>) rowData).get(directionIndex);
                            switch (directionName) {
                                case "INBOUND":
                                    direction = Direction.INBOUND;
                                    break;
                                case "OUTBOUND":
                                default:
                                    direction = Direction.OUTBOUND;
                                    break;
                            }
                        }

                        // get model from the db
                        List<StopModel> stopModels = db.getOrderedStopsForRoute(itemId, direction);
                        if (stopModels.isEmpty()) {
                            System.out.println("No stops found for route: " + itemId);
                        }

                        GeoPosition geoPosition = calculateBaricentro(stopModels);
                        int zoomLevel = calculateZoomLevel(stopModels);

                        List<GtfsRealtime.VehiclePosition> routePositions = realtimeService.getRoutesVehiclePositions(itemId);

                        // render stops and route lines on the map
                        mainFrame.renderRouteLines(stopModels, routePositions, itemId, geoPosition, zoomLevel);

                        yield db.isFavouriteRoute(sessionUser.getId(), itemId);
                    }
                };
                updatePreferButton(itemId, isFav, dataType);
                mainFrame.updatePreferBarVisibility(true);
                updatePreferButton(itemId, isFav, dataType);
            }
        };
        mainFrame.setGenericTableRowClickBehaviour(genericTableRowClickBehaviour);
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
                                updatePreferButton(stop.getId(), db.isFavoriteStop(sessionUser.getId(), stop.getId()),
                                        STOP);
                                mainFrame.updatePreferBarVisibility(true);
                                return;
                            }
                        }
                    }

                    mainFrame.updateStopPanelVisibility(false);
                    mainFrame.updatePreferBarVisibility(false);
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
}
