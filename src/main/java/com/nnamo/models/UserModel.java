package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Represents a user with a unique username and a hashed password.
 */
@DatabaseTable(tableName = "users")
public class UserModel {

    /**
     * Unique identifier for the user (auto-incremented).
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * Unique username for the user.
     */
    @DatabaseField(unique = true)
    private String username;

    /**
     * Hashed password for the user.
     */
    @DatabaseField
    private String passwordHash;

    /**
     * Empty constructor required by OrmLite.
     */
    public UserModel() {
    }

    /**
     * Constructs a UserModel with the specified username and password hash.
     *
     * @param username     the unique username
     * @param passwordHash the hashed password
     */
    public UserModel(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    /**
     * Gets the unique identifier for the user.
     *
     * @return the user ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the username of the user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the hashed password of the user.
     *
     * @return the password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }
}
