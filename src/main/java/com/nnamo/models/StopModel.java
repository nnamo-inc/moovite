package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Represents a GTFS stop location with an ID, name, latitude, longitude, and
 * optional location type.
 */
@DatabaseTable(tableName = "stops")
public class StopModel {

    /**
     * Unique identifier for the stop.
     */
    @DatabaseField(id = true)
    private String id;

    /**
     * Name of the stop.
     */
    @DatabaseField
    private String name;

    /**
     * Latitude coordinate of the stop.
     */
    @DatabaseField
    private double latitude;

    /**
     * Longitude coordinate of the stop.
     */
    @DatabaseField
    private double longitude;

    /**
     * Optional location type for the stop.
     */
    @DatabaseField(canBeNull = true)
    private final String location_type = null;

    /**
     * Empty constructor required by OrmLite.
     */
    public StopModel() {
    }

    /**
     * Constructs a StopModel with the specified ID, name, latitude, and longitude.
     *
     * @param id        the stop identifier
     * @param name      the stop name
     * @param latitude  the latitude coordinate
     * @param longitude the longitude coordinate
     */
    public StopModel(String id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Gets the stop identifier.
     *
     * @return the stop ID
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the stop name.
     *
     * @return the stop name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the latitude coordinate.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude coordinate.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Gets the location type.
     *
     * @return the location type, or null if not set
     */
    public String getLocationType() {
        return location_type;
    }

    /**
     * Returns a string representation of the StopModel.
     *
     * @return string representation
     */
    public String toString() {
        return "StopModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", location_type='" + location_type + '\'' +
                '}';
    }
}
