package com.nnamo;

import java.sql.SQLException;

public class MapController {

    DatabaseService db;
    MapView mapService = new MapView();

    public MapController(DatabaseService db) {
        this.db = db;
    }

    public void run() throws SQLException {
        mapService.renderStops(db.getAllStops());
        mapService.run();
    }
}
