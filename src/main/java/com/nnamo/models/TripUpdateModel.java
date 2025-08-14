package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "trip_updates")
public class TripUpdateModel {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private TripModel trip;

    @DatabaseField
    private int averageDelay;

    @DatabaseField
    private boolean skipped;

    public TripUpdateModel() {
    }

    public TripUpdateModel(TripModel trip, int averageDelay, boolean skipped) {
        this.trip = trip;
        this.averageDelay = averageDelay;
        this.skipped = skipped;
    }

}
