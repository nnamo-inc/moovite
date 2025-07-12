package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// Rappresenta una corsa di una linea (ad esempio, corsa delle 8:00 della linea 163)
@DatabaseTable(tableName = "users")
public class UserModel {

    @DatabaseField(generatedId = true) // AUTO INCREMENT ID
    private int id;

    @DatabaseField
    private String username;

    @DatabaseField
    private String password_hash;

    public UserModel() { // Empty constructor required by OrmLite
    }

    public UserModel(int id, String username, String password_hash) { // Empty constructor required by OrmLite
        this.id = id;
        this.username = username;
        this.password_hash = password_hash;
    }
}
