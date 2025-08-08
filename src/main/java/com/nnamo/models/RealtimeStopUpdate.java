package com.nnamo.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;

public class RealtimeStopUpdate {
    private final String tripId;
    private String routeId;
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
        long posixTime = timeUpdate.getArrival().getTime();
        LocalDateTime dateTime = Instant
                .ofEpochSecond(posixTime, 0)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return dateTime.toLocalTime().toSecondOfDay();
    }

    public long getUpdateDelay() {
        return timeUpdate.getArrival().getDelay();
    }
}
