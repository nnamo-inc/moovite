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

        mainFrame.renderStops(db.getAllStops());

        mainFrame.getSearchPanel().addSearchListener(this::searchQueryListener);
        mainFrame.getLeftPanel().getPreferPanel().addSearchListener(this::searchQueryListener);
        mainFrame.getLeftPanel().getStatisticsPanel().setupListeners(realtimeService);
        mainFrame.getLeftPanel().getStatisticsPanel().setupDatabaseService(db);

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
        this.searchQueryListener("", RouteType.ALL);
    }

    public void setLocalMapCache(File cacheDir) {
        mainFrame.setLocalMapCache(cacheDir);
    }

    public void searchQueryListener(String searchText, RouteType routeType) {
        // if (searchText == null || searchText.isEmpty()) {
        // mainFrame.getSearchPanel().updateView(new ArrayList<>());
        // return; // Exit if the search text is empty
        // }


        var searchPanel = mainFrame.getSearchPanel();
        try {
            var stops = db.getStopsByName(searchText);
            var routes = db.getRoutesByName(searchText, routeType);
            searchPanel.updateView(stops, routes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
