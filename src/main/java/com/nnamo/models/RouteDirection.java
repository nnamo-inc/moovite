package com.nnamo.models;

import com.j256.ormlite.table.DatabaseTable;
import com.nnamo.enums.Direction;
import com.nnamo.enums.RouteType;

/**
 * Represents a directioned route.
 * It is used to differentiate routes based on their direction, for instance
 * when visualizing the routes on the map
 * Contains route ID, agency, names, type, direction, and direction name.
 */
@DatabaseTable(tableName = "routes")
public class RouteDirection {

    /**
     * Unique identifier for the route.
     */
    private final String id;

    /**
     * The agency associated with the route.
     */
    private final AgencyModel agency;

    /**
     * The long name of the route.
     */
    private final String longName;

    /**
     * The short name of the route.
     */
    private final String shortName;

    /**
     * The type of the route.
     */
    private final RouteType type;

    /**
     * The direction of the route (e.g., OUTBOUND, INBOUND).
     */
    private Direction routeDirection = Direction.OUTBOUND;

    /**
     * The name of the direction (e.g., "Eastbound").
     */
    private String directionName;

    /**
     * Constructs a RouteDirection with all required fields.
     *
     * @param id            the route ID
     * @param agency        the agency
     * @param longName      the long name of the route
     * @param shortName     the short name of the route
     * @param routeType     the route type
     * @param direction     the direction
     * @param directionName the direction name
     */
    public RouteDirection(
            String id,
            AgencyModel agency,
            String longName,
            String shortName,
            RouteType routeType,
            Direction direction,
            String directionName) {
        this.id = id;
        this.longName = longName;
        this.shortName = shortName;
        this.agency = agency;
        this.type = routeType;
        this.routeDirection = direction;
        this.directionName = directionName;
    }

    /**
     * Gets the route ID.
     *
     * @return the route ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the long name of the route.
     *
     * @return the long name
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Gets the short name of the route.
     *
     * @return the short name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Gets the agency associated with the route.
     *
     * @return the agency
     */
    public AgencyModel getAgency() {
        return agency;
    }

    /**
     * Gets the route type.
     *
     * @return the route type
     */
    public RouteType getType() {
        return type;
    }

    /**
     * Gets the direction of the route.
     *
     * @return the direction
     */
    public Direction getDirection() {
        return routeDirection;
    }

    /**
     * Gets the direction name.
     *
     * @return the direction name
     */
    public String getDirectionName() {
        return directionName;
    }

    /**
     * Sets the direction of the route.
     *
     * @param direction the direction to set
     */
    public void setDirection(Direction direction) {
        this.routeDirection = direction;
    }

    /**
     * Sets the direction name.
     *
     * @param directionName the direction name to set
     */
    public void setDirectionName(String directionName) {
        this.directionName = directionName;
    }
}
