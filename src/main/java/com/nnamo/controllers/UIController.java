package com.nnamo.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.swing.JPanel;

import org.jxmapviewer.viewer.GeoPosition;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.nnamo.enums.ColumnName;
import com.nnamo.enums.DataType;
import com.nnamo.enums.Direction;
import com.nnamo.enums.UpdateMode;
import com.nnamo.interfaces.*;
import com.nnamo.models.RealtimeStopUpdate;
import com.nnamo.models.RouteDirection;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopModel;
import com.nnamo.models.StopTimeModel;
import com.nnamo.models.TripModel;
import com.nnamo.models.UserModel;
import com.nnamo.services.DatabaseService;
import com.nnamo.services.RealtimeGtfsService;
import com.nnamo.utils.Utils;
import com.nnamo.view.frame.MainFrame;

public class UIController {
    private final DatabaseService db;
    private final RealtimeGtfsService realtimeService;
    private final MainFrame mainFrame;
    private UserModel user;
    private boolean loaded;

    public UIController(DatabaseService db, MainFrame mainFrame, RealtimeGtfsService realtimeService) {
        this.db = db;
        this.mainFrame = mainFrame;
        this.realtimeService = realtimeService;
    }

    public void run() {
        setupTableBehavior();
        setupFavoriteBehavior();
        setupButtonPanelBehaviours();
    }

    public void run(UserModel sessionUser) {
        this.setUser(sessionUser);
        setupTableBehavior();
        setupFavoriteBehavior();
        setupButtonPanelBehaviours();
    }

    public void setUser(UserModel sessionUser) {
        this.user = sessionUser;
    }

    public void setupTableBehavior() {
        mainFrame.setGenericTableRowClickBehaviour(createTableRowClickBehaviour());
        mainFrame.getStopPanel().getTimeTable().setTableRowClickBehaviour(createRouteClickBehaviour());
    }

    public void setupFavoriteBehavior() {
        mainFrame.setGeneralFavBehaviour(createFavoriteBehaviour());
    }

    // Handles behaviour when clicking on the left panel's buttons (which open the
    // panels on the left)
    public void setupButtonPanelBehaviours() {
        var buttonPanelBehaviour = createButtonPanelBehaviour(); // Behaviour of general buttons that open panels on the
                                                                 // Left panel
        mainFrame.setButtonPanelGeneralBehaviour(buttonPanelBehaviour);

        // Behaviour specific to the favorite panel, since duplicates can appear without
        // this
        mainFrame.setButtonPanelPreferBehaviour(createPreferButtonBehaviour(buttonPanelBehaviour));
    }

    private TableRowClickBehaviour createTableRowClickBehaviour() {
        return new TableRowClickBehaviour() {
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
                        handleStopSelection(stop, user);
                        yield db.isFavoriteStop(user.getId(), itemId);
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

                        GeoPosition geoPosition = MapController.calculateBaricentro(stopModels);
                        int zoomLevel = MapController.calculateZoomLevel(stopModels);

                        List<GtfsRealtime.VehiclePosition> routePositions = realtimeService
                                .getRoutesVehiclePositions(itemId, direction);

                        // render stops and route lines on the map
                        mainFrame.renderRouteLines(stopModels, routePositions, itemId, geoPosition, zoomLevel);

                        yield db.isFavouriteRoute(user.getId(), itemId);
                    }
                };
                updatePreferButton(itemId, isFav, dataType);
                mainFrame.updatePreferBarVisibility(true);
                updatePreferButton(itemId, isFav, dataType);
            }
        };
    }

    // Click on row that shows vehicle position of clicked line
    private TableRowClickBehaviour createRouteClickBehaviour() {
        return new TableRowClickBehaviour() {
            @Override
            public void onRowClick(Object rowData, ColumnName[] columnNames, DataType dataType)
                    throws SQLException, IOException {
                List<ColumnName> columnsList = Arrays.asList(columnNames);
                String tripId = (String) ((List<Object>) rowData).get(
                        columnsList.indexOf(ColumnName.TRIP));
                VehiclePosition position = realtimeService.getTripVehiclePosition(tripId);
                if (position != null) {
                    GeoPosition busPosition = new GeoPosition(
                            position.getPosition().getLatitude(),
                            position.getPosition().getLongitude());
                    mainFrame.setMapPanelMapPosition(busPosition, 1);
                    mainFrame.removeRoutePainting();
                    mainFrame.renderVehiclePositions(Arrays.asList(new VehiclePosition[] { position }));
                }

            }
        };
    }

    private FavoriteBehaviour createFavoriteBehaviour() {
        return new FavoriteBehaviour() {
            @Override
            public void addFavorite(String itemId, DataType dataType) {
                try {
                    switch (dataType) {
                        case STOP: {
                            db.addFavStop(user.getId(), itemId);
                            mainFrame.updateFavStopTable(db.getStopById(itemId), UpdateMode.ADD);
                            mainFrame.updatePreferButton(itemId, true, DataType.STOP);
                            System.out.println(db.getFavoriteStops(user.getId()).stream().count());
                            break;
                        }
                        case ROUTE: {
                            db.addFavRoute(user.getId(), itemId);
                            RouteModel route = db.getRouteById(itemId);
                            List<RouteModel> routeList = Arrays.asList(new RouteModel[] { route });
                            List<RouteDirection> directionRoutes = db.getDirectionedRoutes(routeList);
                            mainFrame.updateFavRouteTable(directionRoutes, UpdateMode.ADD);
                            mainFrame.updatePreferButton(itemId, true, DataType.ROUTE);
                            System.out.println(db.getFavoriteRoutes(user.getId()).stream().count());
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
                            db.removeFavStop(user.getId(), itemId);
                            mainFrame.updateFavStopTable(db.getStopById(itemId), UpdateMode.REMOVE);
                            mainFrame.updatePreferButton(itemId, false, dataType);
                            System.out.println(db.getFavoriteStops(user.getId()).stream().count());
                            break;
                        }
                        case ROUTE: {
                            RouteModel route = db.getRouteById(itemId);
                            List<RouteModel> routeList = Arrays.asList(new RouteModel[] { route });
                            List<RouteDirection> directionRoutes = db.getDirectionedRoutes(routeList);
                            db.removeFavRoute(user.getId(), itemId);
                            mainFrame.updateFavRouteTable(directionRoutes, UpdateMode.REMOVE);
                            mainFrame.updatePreferButton(itemId, false, dataType);
                            System.out.println(db.getFavoriteRoutes(user.getId()).stream().count());
                            break;
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private ButtonPanelBehaviour createButtonPanelBehaviour() {
        return new ButtonPanelBehaviour() {
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
    }

    // Specific behaviour when clicking on left panel's button that opens Favorite
    // Panel
    private ButtonPanelBehaviour createPreferButtonBehaviour(ButtonPanelBehaviour baseButtonPanelBehaviour) {
        return new ButtonPanelBehaviour() {
            @Override
            public void onButtonPanelClick(JPanel panel) {
                if (!loaded) {
                    try {
                        mainFrame.initLeftPanelPreferPanelPreferTable(
                                db.getFavoriteStops(user.getId()),
                                db.getFavoriteDirectionRoutes(user.getId()));
                        loaded = true;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                baseButtonPanelBehaviour.onButtonPanelClick(panel); // TODO: not sure, but maybe need to refactor this
            };
        };
    }

    public static void handleStopSelection(StopModel stop, UserModel user, RealtimeGtfsService realtimeService,
            MainFrame mainFrame, DatabaseService db) throws IOException, SQLException {

        Date currentDate = Utils.getCurrentDate();
        LocalTime currentTime = Utils.getCurrentTime();
        List<RealtimeStopUpdate> realtimeUpdates = realtimeService.getStopUpdatesById(stop.getId());
        List<StopTimeModel> stopTimes = db.getNextStopTimes(stop.getId(), currentTime,
                currentDate, realtimeUpdates);
        mainFrame.setCurrentStopId(stop.getId());
        UIController.updateStopPanel(stop, stopTimes, realtimeUpdates, mainFrame, db);
        UIController.updatePreferButton(stop.getId(), db.isFavoriteStop(user.getId(), stop.getId()),
                DataType.STOP, mainFrame);
        mainFrame.updatePreferBarVisibility(true);
    }

    private void handleStopSelection(StopModel stop, UserModel user) throws IOException, SQLException {
        UIController.handleStopSelection(stop, user, this.realtimeService, this.mainFrame, this.db);
    }

    private void updateStopPanel(StopModel stop, List<StopTimeModel> stopTimes,
            List<RealtimeStopUpdate> realtimeUpdates) throws SQLException, IOException {
        UIController.updateStopPanel(stop, stopTimes, realtimeUpdates, this.mainFrame, this.db);
    }

    public static void updateStopPanel(StopModel stop, List<StopTimeModel> stopTimes,
            List<RealtimeStopUpdate> realtimeUpdates, MainFrame mainFrame, DatabaseService db)
            throws SQLException, IOException {
        mainFrame.updateStopPanelInfo(stop.getId(), stop.getName());
        mainFrame.updateStopPanelTimes(stopTimes, realtimeUpdates);
        mainFrame.updateStopPanelVisibility(true);

        HashSet<String> uniqueRouteIds = new HashSet<>();
        List<List<String>> uniqueRoutes = new ArrayList<>();

        for (StopTimeModel stopTime : stopTimes) {
            if (stopTime.getTrip() == null || stopTime.getTrip().getRoute() == null) {
                continue; // Skip if trip or route is null
            }
            if (!uniqueRouteIds.add(stopTime.getTrip().getRoute().getId())) {
                continue; // Skip if route ID is already processed
            }

            TripModel trip = stopTime.getTrip();
            RouteModel route = trip.getRoute();

            uniqueRoutes.add(Arrays.asList(new String[] {
                    route.getLongName() != null ? route.getLongName() : route.getShortName(),
                    route.getId(),
                    route.getType().toString(),
                    trip.getHeadsign(),
                    trip.getDirection().name(),
                    RealtimeGtfsService.getRouteQuality(db.getAverageDelayForRoute(route.getId())).toString()
            }));
        }

        System.out.println("Unique routes found: " + uniqueRoutes.size());

        mainFrame.updateStopPanelRoutes(uniqueRoutes);
    }

    public static void updatePreferButton(String itemId, boolean isFav, DataType dataType, MainFrame mainFrame) {
        mainFrame.updatePreferButton(itemId, isFav, dataType);
    }

    private void updatePreferButton(String itemId, boolean isFav, DataType dataType) {
        updatePreferButton(itemId, isFav, dataType, mainFrame);
    }
}
