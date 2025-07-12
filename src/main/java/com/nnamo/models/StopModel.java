package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "stops")
public class StopModel {

    @DatabaseField(id = true)
    private String id;

    @DatabaseField
    private String name;

    @DatabaseField
    private double latitude;

    @DatabaseField
    private double longitude;

    @DatabaseField(canBeNull = true)
    private final String location_type = null;

    public StopModel() { // Empty constructor required by OrmLite
    }

    public StopModel(String id, String name, double latitude, double longitude) { // Empty constructor required by OrmLite
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
