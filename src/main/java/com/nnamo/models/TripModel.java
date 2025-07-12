package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// Rappresenta una corsa di una linea (ad esempio, corsa delle 8:00 della linea 163)
@DatabaseTable(tableName = "trips")
public class TripModel {

    @DatabaseField(id = true)
    private String id;

    @DatabaseField (foreign = true)
    private ServiceModel service_id; // 

    @DatabaseField (foreign = true)
    private RouteModel route_id;

    @DatabaseField(canBeNull = true)
    private String headsign; // Stringa che segnala la direzione della corsa

    @DatabaseField(canBeNull = true)
    private String direction;

    public TripModel() { // Empty constructor required by OrmLite
    }

    public TripModel(String id, String name, double latitude, double longitude) { // Empty constructor required by OrmLite
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }


}
