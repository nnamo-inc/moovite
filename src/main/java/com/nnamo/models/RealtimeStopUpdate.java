package com.nnamo.models;

import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.nnamo.utils.Utils;

public class RealtimeStopUpdate {
    private final String tripId;
    private final String routeId;
    private final String stopId;
    private final StopTimeUpdate timeUpdate;
    private final VehiclePosition vehiclePosition;

    public RealtimeStopUpdate(String tripId, String routeId, StopTimeUpdate timeUpdate,
                              VehiclePosition vehiclePosition) {
        this.tripId = tripId;
        this.routeId = routeId;
        this.stopId = timeUpdate.getStopId();
        this.vehiclePosition = vehiclePosition;
        this.timeUpdate = timeUpdate;
    }

    public String getTripId() {
        return tripId;
    }

    public String getStopId() {
        return stopId;
    }

    public String getRouteId() {
        return routeId;
    }

    public StopTimeUpdate getTimeUpdate() {
        return timeUpdate;
    }

    public VehiclePosition getVehiclePosition() {
        return vehiclePosition;
    }

    public int getArrivalTime() {
        return Utils.posixToSecondsOfDay(timeUpdate.getArrival().getTime());
    }

    public int getDepartureTime() {
        return Utils.posixToSecondsOfDay(timeUpdate.getDeparture().getTime());
    }

    public long getUpdateDelay() {
        return timeUpdate.getArrival().getDelay();
    }
}
