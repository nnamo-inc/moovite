package com.nnamo.controllers;

import com.nnamo.interfaces.WaypointListener;
import com.nnamo.view.MapView;
import com.nnamo.services.DatabaseService;

import java.sql.SQLException;

public class MapController {

    DatabaseService db;
    MapView mapView = new MapView();

    public MapController(DatabaseService db) {
        this.db = db;
    }

    public void run() throws SQLException { // TODO: need to be continued
        mapView.renderStops(db.getAllStops());
        mapView.setWaypointListener(new WaypointListener() {
            @Override
            public void waypointClicked(double x, double y) {
                System.out.println("Clicked on waypoint at coordinates: " + x + ", " + y);
            }
        });
        mapView.run();
    }
}