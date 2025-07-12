package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// Rappresenta una corsa di una linea (ad esempio, corsa delle 8:00 della linea 163)
@DatabaseTable(tableName = "users")
public class User {

    @DatabaseField(generatedId = true) // AUTO INCREMENT ID
    private int id;

    @DatabaseField
    private String username;

    @DatabaseField
    private String password; // Hashed

    public User() { // Empty constructor required by OrmLite
    }

    public User(int id, String username, String password) { // Empty constructor required by OrmLite
        this.id = id;
        this.username = username;
        this.password = password;
    }
}
