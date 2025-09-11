package com.nnamo.models;

import org.jxmapviewer.viewer.GeoPosition;

/**
 * Represents a static vehicle position using latitude and longitude
 * coordinates.
 */
public class StaticVehiclePosition {
    /**
     * Latitude of the vehicle position.
     */
    private final double latitude;

    /**
     * Longitude of the vehicle position.
     */
    private final double longitude;

    /**
     * Constructs a StaticVehiclePosition with specified latitude and longitude.
     *
     * @param latitude  the latitude coordinate
     * @param longitude the longitude coordinate
     */
    public StaticVehiclePosition(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Constructs a StaticVehiclePosition from a GeoPosition object.
     *
     * @param position the GeoPosition object
     */
    public StaticVehiclePosition(GeoPosition position) {
        this.latitude = position.getLatitude();
        this.longitude = position.getLongitude();
    }

    /**
     * Returns the latitude of the vehicle position.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * Returns the longitude of the vehicle position.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return this.longitude;
    }

    /**
     * Returns a GeoPosition object representing this vehicle position.
     *
     * @return the GeoPosition object
     */
    public GeoPosition getPosition() {
        return new GeoPosition(this.latitude, this.longitude);
    }
}
