package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Represents a user's favorite route.
 * Used for storing the association between a user and a route in the database.
 */
@DatabaseTable(tableName = "favorite_routes")
public class FavoriteRouteModel {

    /**
     * Auto-incremented unique identifier for the favorite route entry.
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * The user who marked the route as favorite.
     */
    @DatabaseField(foreign = true, uniqueCombo = true)
    private UserModel user;

    /**
     * The route marked as favorite by the user.
     */
    @DatabaseField(foreign = true, uniqueCombo = true, foreignAutoRefresh = true)
    private RouteModel route;

    /**
     * Empty constructor required by OrmLite.
     */
    public FavoriteRouteModel() {
    }

    /**
     * Constructs a FavoriteRouteModel with the specified user and route.
     *
     * @param user  the user who favorites the route
     * @param route the route marked as favorite
     */
    public FavoriteRouteModel(UserModel user, RouteModel route) {
        this.user = user;
        this.route = route;
    }

    /**
     * Gets the unique identifier of this favorite route entry.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the user who marked the route as favorite.
     *
     * @return the user
     */
    public UserModel getUser() {
        return user;
    }

    /**
     * Gets the route marked as favorite.
     *
     * @return the route
     */
    public RouteModel getRoute() {
        return route;
    }
}
