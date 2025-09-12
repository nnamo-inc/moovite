package com.nnamo.models;

import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.nnamo.utils.Utils;

/**
 * Represents a real-time update for a stop on a transit trip.
 * Contains trip, route, stop, time update, and vehicle position information.
 * It's not an ORMLite model, but a model used to have everything related to a
 * trip stop update in the same class
 */
public class RealtimeStopUpdate {
    private final String tripId;
    private final String routeId;
    private final String stopId;
    private final StopTimeUpdate timeUpdate;
    private final VehiclePosition vehiclePosition;

    /**
     * Constructs a RealtimeStopUpdate with the specified trip, route, time update,
     * and vehicle position.
     *
     * @param tripId          the trip identifier
     * @param routeId         the route identifier
     * @param timeUpdate      the stop time update
     * @param vehiclePosition the vehicle position
     */
    public RealtimeStopUpdate(String tripId, String routeId, StopTimeUpdate timeUpdate,
                              VehiclePosition vehiclePosition) {
        this.tripId = tripId;
        this.routeId = routeId;
        this.stopId = timeUpdate.getStopId();
        this.vehiclePosition = vehiclePosition;
        this.timeUpdate = timeUpdate;
    }

    /**
     * Gets the trip identifier.
     *
     * @return the trip ID
     */
    public String getTripId() {
        return tripId;
    }

    /**
     * Gets the stop identifier.
     *
     * @return the stop ID
     */
    public String getStopId() {
        return stopId;
    }

    /**
     * Gets the route identifier.
     *
     * @return the route ID
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     * Gets the stop time update.
     *
     * @return the stop time update
     */
    public StopTimeUpdate getTimeUpdate() {
        return timeUpdate;
    }

    /**
     * Gets the vehicle position.
     *
     * @return the vehicle position
     */
    public VehiclePosition getVehiclePosition() {
        return vehiclePosition;
    }

    /**
     * Gets the arrival time in seconds of day.
     *
     * @return the arrival time in seconds of day
     */
    public int getArrivalTime() {
        return Utils.posixToSecondsOfDay(timeUpdate.getArrival().getTime());
    }

    /**
     * Gets the departure time in seconds of day.
     *
     * @return the departure time in seconds of day
     */
    public int getDepartureTime() {
        return Utils.posixToSecondsOfDay(timeUpdate.getDeparture().getTime());
    }

    /**
     * Gets the delay for the update.
     *
     * @return the delay in seconds
     */
    public long getUpdateDelay() {
        return timeUpdate.getArrival().getDelay();
    }
}
