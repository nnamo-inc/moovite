package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// Rappresenta una corsa di una linea (ad esempio, corsa delle 8:00 della linea 163)
@DatabaseTable(tableName = "trips")
public class TripModel {

    @DatabaseField(id = true)
    private String id;

    /*
     * @DatabaseField(foreign = true)
     * private ServiceModel service_id; //
     */

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private RouteModel route;

    @DatabaseField(canBeNull = true)
    private String headsign; // Stringa che segnala la direzione della corsa

    @DatabaseField(canBeNull = true)
    private String direction;

    public TripModel() { // Empty constructor required by OrmLite
    }

    public TripModel(String id, RouteModel route, String headsign, String direction) {
        this.id = id;
        // this.service_id = service_id;
        this.route = route;
        this.headsign = headsign;
        this.direction = direction;
    }

    public String getId() {
        return id;
    }

    public RouteModel getRoute() {
        return route;
    }

    public String getHeadsign() {
        return headsign;
    }

    public String getDirection() {
        return direction;
    }
}
