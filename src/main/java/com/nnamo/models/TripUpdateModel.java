package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Represents a delay update for a trip, including route, creation time, and
 * delay value.
 */
@DatabaseTable(tableName = "trip_updates_delays")
public class TripUpdateModel {

    /**
     * Unique identifier for the trip update.
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * The route associated with this trip update.
     */
    @DatabaseField(foreign = true, foreignAutoRefresh = true, index = true)
    private RouteModel route;

    /**
     * Timestamp when the update was created.
     */
    @DatabaseField
    private Date createdAt;

    /**
     * Delay value in seconds.
     */
    @DatabaseField
    private int delay;

    /**
     * Empty constructor required by OrmLite.
     */
    public TripUpdateModel() {
    }

    /**
     * Constructs a TripUpdateModel with the specified route, creation time, and
     * delay.
     *
     * @param route     the route associated with the update
     * @param createdAt the creation time as LocalDateTime
     * @param delay     the delay value in seconds
     */
    public TripUpdateModel(RouteModel route, LocalDateTime createdAt, int delay) {
        this.route = route;
        this.createdAt = Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
        this.delay = delay;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return the creation date
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the creation timestamp (duplicate accessor).
     *
     * @return the creation date
     */
    public Date getCreatedAtDate() {
        return createdAt;
    }

    /**
     * Gets the delay value in seconds.
     *
     * @return the delay
     */
    public int getDelay() {
        return delay;
    }
}
