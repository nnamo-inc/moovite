package com.nnamo.controllers;

import com.nnamo.enums.RealtimeMetricType;
import com.nnamo.enums.RealtimeStatus;
import com.nnamo.interfaces.LogoutBehaviour;
import com.nnamo.interfaces.SearchBarListener;
import com.nnamo.interfaces.SessionListener;
import com.nnamo.models.RouteDirection;
import com.nnamo.models.StopModel;
import com.nnamo.models.UserModel;
import com.nnamo.services.DatabaseService;
import com.nnamo.services.RealtimeGtfsService;
import com.nnamo.view.customcomponents.statistic.MetricCollector;
import com.nnamo.view.frame.MainFrame;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main controller that manages the overall application flow.
 * It coordinates between the user, map, UI, and real-time controllers,
 * handles user sessions, and initializes the main application frame.
 * It also sets up listeners for search and preference panels, and manages real-time data updates.
 *
 * @author Samuele Lombardi
 * @author Riccardo Finocchiaro
 * @author Davide Galilei
 * @see UserController
 * @see MapController
 * @see UIController
 * @see RealtimeController
 * @see MainFrame
 * @see DatabaseService
 * @see RealtimeGtfsService
 */
public class MainController {

    UserController userController;
    MapController mapController;
    UIController uiController;
    RealtimeController realtimeController;

    DatabaseService db;
    RealtimeGtfsService realtimeService;
    MainFrame mainFrame;
    UserModel sessionUser;
    boolean loaded = false;

    // CONSTRUCTORS //
    public MainController(DatabaseService db, RealtimeGtfsService realtimeService) throws IOException {
        this.db = db;
        this.realtimeService = realtimeService;
        this.mainFrame = new MainFrame();

        this.userController = new UserController(db);
        this.mapController = new MapController(db, mainFrame, realtimeService);
        this.uiController = new UIController(db, mainFrame, realtimeService);
        this.realtimeController = new RealtimeController(db, mainFrame, realtimeService);
    }

    // METHODS //
    public void run() throws InterruptedException, SQLException, IOException {

        SearchBarListener searchQueryListener = createSearchPanelQueryBehavior();

        mainFrame.renderStops(db.getAllStops());
        mainFrame.setSearchPanelListener(searchQueryListener);
        mainFrame.setPreferPanelListener(createPreferPanelQueryBehavior());
        mainFrame.setupStatisticsPanel(realtimeService, db.getAllMetrics(), new MetricCollector() {
            @Override
            public void onProducedMetric(RealtimeMetricType type, int value) {
                try {
                    db.saveMetric(type, value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // Login and Session Fetching
        userController.addSessionListener(new SessionListener() { // [!] Listener must be implemented before run()
            @Override
            public void onSessionCreated(int userId) {
                try {
                    sessionUser = db.getUserById(userId);
                    mapController.setSessionUser(sessionUser);
                    uiController.setUser(sessionUser);
                    realtimeController.setUser(sessionUser);
                    mainFrame.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });
        userController.run();
        uiController.run();
        mapController.run();
        realtimeController.run();

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

        realtimeService.startFeedThread();
        mainFrame.setRealtimeStatus(RealtimeStatus.ONLINE); // Changing realtime status notifies the observer method,

        // Mostra tutte le fermate allo startup del programma.
        searchQueryListener.onSearch("");
    }

    public void setLocalMapCache(File cacheDir) {
        mainFrame.setLocalMapCache(cacheDir);
    }

    /**
     * Creates a {@link SearchBarListener} that handles search queries for the search panel.
     * It uses caching to store previously fetched results for stops and routes to improve performance on repeated searches.
     * When a search is performed, it checks the cache first before querying the database.
     * The results are then rendered in the search panel of the main frame.
     *
     * @return a {@link SearchBarListener} for handling search queries in the search panel
     * @author Davide Galilei
     * @see SearchBarListener
     * @see StopModel
     * @see RouteDirection
     * @see DatabaseService
     */
    public SearchBarListener createSearchPanelQueryBehavior() {
        Map<String, List<StopModel>> stopCache = new HashMap<>();
        Map<String, List<RouteDirection>> routeCache = new HashMap<>();

        return new SearchBarListener() {
            @Override
            public void onSearch(String searchText) {
                try {
                    List<StopModel> stops;
                    List<RouteDirection> routes;

                    if (stopCache.containsKey(searchText)) {
                        stops = stopCache.get(searchText);
                    } else {
                        stops = db.getStopsByName(searchText);
                        stopCache.put(searchText, stops);
                    }

                    if (routeCache.containsKey(searchText)) {
                        routes = routeCache.get(searchText);
                    } else {
                        routes = db.getRoutesByName(searchText);
                        routeCache.put(searchText, routes);
                    }

                    mainFrame.renderSearchPanel(stops, routes);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Creates a {@link SearchBarListener} that handles search queries for the prefer panel.
     * It fetches favorite stops and routes for the logged-in user based on the search text.
     * The results are then displayed in the prefer panel of the main frame.
     * This listener does not use caching since favorite items are user-specific and may change frequently.
     *
     * @return a {@link SearchBarListener} for handling search queries in the prefer panel
     * @author Davide Galilei
     * @see SearchBarListener
     * @see StopModel
     * @see RouteDirection
     * @see DatabaseService
     */
    public SearchBarListener createPreferPanelQueryBehavior() {
        return new SearchBarListener() {
            @Override
            public void onSearch(String searchText) {
                if (sessionUser == null) {
                    return;
                }

                try {
                    // For favorite stops, we don't filter by route type since stops don't have
                    // route types
                    var favoriteStops = db.getFavoriteStopsByName(sessionUser.getId(), searchText);
                    // For favorite routes, we do filter by the selected route type
                    var favoriteRoutes = db.getFavoriteRoutesByName(sessionUser.getId(), searchText);

                    mainFrame.clearPreferPanelTable();
                    mainFrame.initPreferPanelTable(favoriteStops, favoriteRoutes);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
