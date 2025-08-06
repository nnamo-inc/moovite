package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// Rappresenta una corsa di una linea (ad esempio, corsa delle 8:00 della linea 163)
@DatabaseTable(tableName = "favorite_routes")
public class FavoriteRouteModel {

    @DatabaseField(generatedId = true) // AUTO INCREMENT ID
    private int id;

    @DatabaseField(foreign = true, uniqueCombo = true)
    private UserModel user;

    @DatabaseField(foreign = true, uniqueCombo = true, foreignAutoRefresh = true)
    private RouteModel route;

    public FavoriteRouteModel() { // Empty constructor required by OrmLite
    }

    public FavoriteRouteModel(UserModel user, RouteModel route) { // Empty constructor required by OrmLite
        this.user = user;
        this.route = route;
    }

    public int getId() {
        return id;
    }

    public UserModel getUser() {
        return user;
    }

    public RouteModel getRoute() {
        return route;
    }
}
