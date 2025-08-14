package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nnamo.models.TripModel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@DatabaseTable(tableName = "trip_updates_delays")
public class TripUpdateModel {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, index = true)
    private RouteModel route;

    @DatabaseField
    private Date createdAt;

    @DatabaseField
    private int delay;

    public TripUpdateModel() {
    }

    public TripUpdateModel(RouteModel route, LocalDateTime createdAt, int delay) {
        this.route = route;
        this.createdAt = Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
        this.delay = delay;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getCreatedAtDate() {
        return createdAt;
    }

    public int getDelay() {
        return delay;
    }
}
