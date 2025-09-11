package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Represents a user's favorite stop.
 * Used for storing the association between a user and a stop in the database.
 */
@DatabaseTable(tableName = "favorite_stops")
public class FavoriteStopModel {

    /**
     * Auto-incremented unique identifier for the favorite stop entry.
     */
    @DatabaseField(generatedId = true) // AUTO INCREMENT ID
    private int id;

    /**
     * The user who marked the stop as favorite.
     */
    @DatabaseField(foreign = true, uniqueCombo = true)
    private UserModel user;

    /**
     * The stop marked as favorite by the user.
     */
    @DatabaseField(foreign = true, uniqueCombo = true, foreignAutoRefresh = true)
    private StopModel stop;

    /**
     * Empty constructor required by OrmLite.
     */
    public FavoriteStopModel() {
    }

    /**
     * Constructs a FavoriteStopModel with the specified user and stop.
     *
     * @param user the user who favorites the stop
     * @param stop the stop marked as favorite
     */
    public FavoriteStopModel(UserModel user, StopModel stop) {
        this.user = user;
        this.stop = stop;
    }

    /**
     * Gets the unique identifier of this favorite stop entry.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the user who marked the stop as favorite.
     *
     * @return the user
     */
    public UserModel getUser() {
        return user;
    }

    /**
     * Gets the stop marked as favorite.
     *
     * @return the stop
     */
    public StopModel getStop() {
        return stop;
    }
}
