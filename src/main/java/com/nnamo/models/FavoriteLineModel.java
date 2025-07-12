package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// Rappresenta una corsa di una linea (ad esempio, corsa delle 8:00 della linea 163)
@DatabaseTable(tableName = "favorite_lines")
public class FavoriteLineModel {

    @DatabaseField(generatedId = true) // AUTO INCREMENT ID
    private int id;

    @DatabaseField(foreign = true)
    private UserModel user;

    @DatabaseField(foreign = true)
    private TripModel route;

    public FavoriteLineModel() { // Empty constructor required by OrmLite
    }

    public FavoriteLineModel(UserModel user, TripModel route) { // Empty constructor required by OrmLite
        this.user = user;
        this.route = route;
    }
}
