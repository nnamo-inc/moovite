package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nnamo.utils.Utils;

@DatabaseTable(tableName = "stop_times")
public class StopTimeModel {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, index = true)
    private StopModel stop;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private TripModel trip;

    @DatabaseField(columnName = "arrival_time", canBeNull = true)
    private int arrivalTime; // Seconds after midnight

    @DatabaseField(columnName = "departure_time", canBeNull = true)
    private int departureTime; // Seconds after midnight

    public StopTimeModel() { // Empty constructor required by OrmLite
    }

    public StopTimeModel(TripModel trip, StopModel stop, int arrival_time, int departure_time) {
        this.trip = trip;
        this.stop = stop;
        this.arrivalTime = arrival_time;
        this.departureTime = departure_time;
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
        return arrivalTime;
    }

    public String getArrivalTimeAsStr() {
        return Utils.secondsOfDayToString(arrivalTime);
    }

    public int getDepartureTime() {
        return departureTime;
    }

    public String getDepartureTimeAsStr() {
        return Utils.secondsOfDayToString(departureTime);
    }

    @Override
    public String toString() {
        return String.format("Stop ID: %s, Trip ID: %s, Arrival Time: %s, Departure Time: %s",
                stop.getId(), trip.getId(), getArrivalTimeAsStr(), getDepartureTimeAsStr());
    }
}
