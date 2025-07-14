package com.nnamo.controllers;

import com.nnamo.models.StopModel;
import com.nnamo.view.MapView;
import com.nnamo.services.DatabaseService;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

import java.awt.*;
import java.awt.geom.Point2D;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public void checkWayPointClicked(Point point) throws SQLException {
        for (StopModel stop : db.getAllStops()) {
            GeoPosition stopPosition = new GeoPosition(stop.getLatitude(), stop.getLongitude());
            Point stopPoint = (Point) mapService.getViewer().convertGeoPositionToPoint(stopPosition);
            if (point.distance(stopPoint) < 10) {
                System.out.println("Vicino al waypoint: " + stop.getLatitude() + ", " + stop.getLongitude());;
                break;
            }
        };
    }
};
