package com.nnamo.controllers;

import com.nnamo.enums.*;
import com.nnamo.interfaces.*;
import com.nnamo.models.*;
import com.nnamo.view.frame.MainFrame;
import com.nnamo.services.DatabaseService;
import com.nnamo.services.RealtimeGtfsService;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

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
        mainFrame.setupStatisticsPanel(realtimeService, db);

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
        searchQueryListener.onSearch("", RouteType.ALL);
    }

    public void setLocalMapCache(File cacheDir) {
        mainFrame.setLocalMapCache(cacheDir);
    }

    public SearchBarListener createSearchPanelQueryBehavior() {
        return new SearchBarListener() {
            @Override
            public void onSearch(String searchText, RouteType routeType) {
                try {
                    var stops = db.getStopsByName(searchText);
                    var routes = db.getRoutesByName(searchText, routeType);
                    mainFrame.renderSearchPanel(stops, routes);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public SearchBarListener createPreferPanelQueryBehavior() {
        return new SearchBarListener() {
            @Override
            public void onSearch(String searchText, RouteType routeType) {
                if (sessionUser == null) {
                    return;
                }

                try {
                    // For favorite stops, we don't filter by route type since stops don't have route types
                    var favoriteStops = db.getFavoriteStopsByName(sessionUser.getId(), searchText, RouteType.ALL);
                    // For favorite routes, we do filter by the selected route type
                    var favoriteRoutes = db.getFavoriteRoutesByName(sessionUser.getId(), searchText, routeType);

                    mainFrame.clearPreferPanelTable();
                    mainFrame.initPreferPanelTable(favoriteStops, favoriteRoutes);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
