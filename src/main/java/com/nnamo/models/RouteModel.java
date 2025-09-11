package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nnamo.enums.RouteType;

/**
 * Represents a GTFS route entity.
 * Used with ORMLite for database persistence.
 */
@DatabaseTable(tableName = "routes")
public class RouteModel {

    /**
     * Unique identifier for the route.
     */
    @DatabaseField(id = true)
    private String id;

    /**
     * Agency associated with the route.
     */
    @DatabaseField(foreign = true, canBeNull = true)
    private AgencyModel agency;

    /**
     * Long name of the route.
     */
    @DatabaseField(canBeNull = true)
    private String longName;

    /**
     * Short name of the route.
     */
    @DatabaseField(canBeNull = true)
    private String shortName;

    /**
     * Type of the route (stored as VARCHAR).
     */
    @DatabaseField
    private RouteType type;

    /**
     * Empty constructor required by OrmLite.
     */
    public RouteModel() {
    }

    /**
     * Constructs a RouteModel with all fields.
     *
     * @param id        the route ID
     * @param agency    the agency
     * @param longName  the long name
     * @param shortName the short name
     * @param routeType the route type
     */
    public RouteModel(String id, AgencyModel agency, String longName, String shortName, RouteType routeType) {
        this.id = id;
        this.longName = longName;
        this.shortName = shortName;
        this.agency = agency;
        this.type = routeType;
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
}
