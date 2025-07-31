package com.nnamo.models;

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

    @DatabaseField(canBeNull = true)
    private int arrival_time; // Seconds after midnight

    @DatabaseField(canBeNull = true)
    private int departure_time; // Seconds after midnight

    public StopTimeModel() { // Empty constructor required by OrmLite
    }

    public StopTimeModel(TripModel trip, StopModel stop, int arrival_time, int departure_time) {
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

    public int getArrivalTime() {
        return arrival_time;
    }

    public String getArrivalTimeAsStr() {
        return String.format("%02d:%02d", arrival_time / 3600, (arrival_time % 3600) / 60);
    }

    public int getDepartureTime() {
        return departure_time;
    }

    public String getDepartureTimeAsStr() {
        return String.format("%02d:%02d", departure_time / 3600, (departure_time % 3600) / 60);
    }

    @Override
    public String toString() {
        return String.format("Stop ID: %s, Trip ID: %s, Arrival Time: %s, Departure Time: %s",
                stop.getId(), trip.getId(), getArrivalTimeAsStr(), getDepartureTimeAsStr());
    }
}
