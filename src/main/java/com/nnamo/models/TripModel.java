package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nnamo.enums.Direction;

/**
 * Represents a trip of a route (e.g., the 8:00 run of line 163).
 */
@DatabaseTable(tableName = "trips")
public class TripModel {

    /**
     * Unique identifier for the trip.
     */
    @DatabaseField(id = true, index = true)
    private String id;

    /**
     * The route associated with this trip.
     */
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private RouteModel route;

    /**
     * Service identifier for the trip.
     */
    @DatabaseField(columnName = "service_id")
    private String serviceId;

    /**
     * Headsign indicating the direction of the trip.
     */
    @DatabaseField(canBeNull = true)
    private String headsign; // Stringa che segnala la direzione della corsa

    /**
     * Direction of the trip.
     */
    @DatabaseField(canBeNull = true)
    private Direction direction;

    /**
     * Empty constructor required by OrmLite.
     */
    public TripModel() {
    }

    /**
     * Constructs a TripModel with the specified parameters.
     *
     * @param id        the trip identifier
     * @param route     the route associated with the trip
     * @param serviceId the service identifier
     * @param headsign  the headsign indicating direction
     * @param direction the direction of the trip
     */
    public TripModel(String id, RouteModel route, String serviceId, String headsign, Direction direction) {
        this.id = id;
        this.serviceId = serviceId;
        this.route = route;
        this.headsign = headsign;
        this.direction = direction;
    }

    /**
     * Gets the trip identifier.
     *
     * @return the trip ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the route associated with this trip.
     *
     * @return the RouteModel
     */
    public RouteModel getRoute() {
        return route;
    }

    /**
     * Gets the headsign indicating the direction of the trip.
     *
     * @return the headsign
     */
    public String getHeadsign() {
        return headsign;
    }

    /**
     * Gets the direction of the trip.
     *
     * @return the Direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Gets the service identifier for the trip.
     *
     * @return the service ID
     */
    public String getServiceId() {
        return serviceId;
    }
}
