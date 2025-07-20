package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "routes")
public class RouteModel {

    @DatabaseField(id = true)
    private String id;

    // Agency Foreign Key required if there are multiple agencies, recommended
    // otherwise
    @DatabaseField(foreign = true, canBeNull = true)
    private AgencyModel agency;

    @DatabaseField(canBeNull = true)
    private String longName;

    @DatabaseField(canBeNull = true)
    private String shortName;

    // This should be a foreign key that represents the type of the route
    // https://gtfs.org/documentation/schedule/reference/#routestxt
    // @DatabaseField
    // private RouteType type;

    public RouteModel() { // Empty constructor required by OrmLite
    }

    public RouteModel(String id, AgencyModel agency, String longName, String shortName) { // Empty constructor required
                                                                                          // by OrmLite
        this.id = id;
        this.longName = longName;
        this.shortName = shortName;
        this.agency = agency;
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
}
