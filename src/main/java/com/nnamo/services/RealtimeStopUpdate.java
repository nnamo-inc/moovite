package com.nnamo.services;

import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;

public class RealtimeStopUpdate {
    private String tripId;
    private String routeId;
    private String stopId;
    private StopTimeUpdate timeUpdate;

    public RealtimeStopUpdate(String tripId, StopTimeUpdate timeUpdate) {
        this.tripId = tripId;
        this.stopId = timeUpdate.getStopId();
        this.timeUpdate = timeUpdate;
    }

    public String getTripId() {
        return tripId;
    }

    public String getStopId() {
        return stopId;
    }

    public StopTimeUpdate getTimeUpdate() {
        return timeUpdate;
    }

    public long getUpdateTime() {
        return timeUpdate.getArrival().getTime();
    }

    public long getUpdateDelay() {
        return timeUpdate.getArrival().getDelay();
    }
}
