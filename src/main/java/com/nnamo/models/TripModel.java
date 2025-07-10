package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "trips")
public class TripModel {

    @DatabaseField(id = true)
    private String id;

    @DatabaseField
    private String name;

    @DatabaseField
    private double latitude;

    @DatabaseField
    private double longitude;

    @DatabaseField(canBeNull = true)
    private String location_type = null;

    public TripModel() { // Empty constructor required by OrmLite
    }

    public TripModel(String id, String name, double latitude, double longitude) { // Empty constructor required by OrmLite
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }


}
