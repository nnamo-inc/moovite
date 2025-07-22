package com.nnamo.models;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "stop_times")
public class StopTimeModel {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, index = true)
    private StopModel stop;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private TripModel trip;

    @DatabaseField(dataType = DataType.DATE_STRING, canBeNull = true)
    private Date arrival_time;

    @DatabaseField(dataType = DataType.DATE_STRING, canBeNull = true)
    private Date departure_time;

    public StopTimeModel() { // Empty constructor required by OrmLite
    }

    public StopTimeModel(TripModel trip, StopModel stop, Date arrival_time, Date departure_time) {
        this.trip = trip;
        this.stop = stop;
        this.arrival_time = arrival_time;
        this.departure_time = departure_time;
    }

    public int getId() {
        return id;
    }

    public StopModel getStop() {
        return stop;
    }

    public TripModel getTrip() {
        return trip;
    }

    public Date getArrivalTime() {
        return arrival_time;
    }

    public Date getDepartureTime() {
        return departure_time;
    }

    @Override
    public String toString() {
        return String.format("Stop ID: %s, Trip ID: %s, Arrival Time: %s, Departure Time: %s",
                stop.getId(), trip.getId(), arrival_time, departure_time);
    }
}
