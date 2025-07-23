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
    private String passwordHash;

    public UserModel() { // Empty constructor required by OrmLite
    }

    public UserModel(String username, String passwordHash) { // Empty constructor required by OrmLite
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
