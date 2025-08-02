package com.nnamo.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;

public class RealtimeStopUpdate {
    private final String tripId;
    private String routeId;
    private final String stopId;
    private final StopTimeUpdate timeUpdate;

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
