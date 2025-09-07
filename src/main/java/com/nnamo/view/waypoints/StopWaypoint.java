package com.nnamo.view.waypoints;

import org.jxmapviewer.viewer.DefaultWaypoint;

public class StopWaypoint extends DefaultWaypoint {
    private final String stopId;

    public StopWaypoint(String stopId, double latitude, double longitude) {
        super(latitude, longitude);
        this.stopId = stopId;
    }

    public String getStopId() {
        return stopId;
    }
}
