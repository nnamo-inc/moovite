package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nnamo.enums.Direction;
import com.nnamo.enums.RouteType;

@DatabaseTable(tableName = "routes")
public class RouteDirection {

    private String id;
    private AgencyModel agency;
    private String longName;
    private String shortName;
    private RouteType type;
    private Direction routeDirection = Direction.OUTBOUND;
    private String directionName;

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

    public String getId() {
        return id;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    public AgencyModel getAgency() {
        return agency;
    }

    public RouteType getType() {
        return type;
    }

    public Direction getDirection() {
        return routeDirection;
    }

    public String getDirectionName() {
        return directionName;
    }

    public void setDirection(Direction direction) {
        this.routeDirection = direction;
    }

    public void setDirectionName(String directionName) {
        this.directionName = directionName;
    }
}
