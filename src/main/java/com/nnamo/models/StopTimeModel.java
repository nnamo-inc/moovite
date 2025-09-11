package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nnamo.utils.Utils;

/**
 * Represents a GTFS stop time entry for a trip, including arrival and departure
 * times at a stop.
 * Example: Trip 123 arrives at stop 456 at 19:84
 */
@DatabaseTable(tableName = "stop_times")
public class StopTimeModel {

    /**
     * Unique identifier for the stop time entry.
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * The stop associated with this stop time.
     */
    @DatabaseField(foreign = true, index = true)
    private StopModel stop;

    /**
     * The trip associated with this stop time.
     */
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private TripModel trip;

    /**
     * Arrival time at the stop, in seconds after midnight.
     */
    @DatabaseField(columnName = "arrival_time", canBeNull = true)
    private int arrivalTime; // Seconds after midnight

    /**
     * Departure time from the stop, in seconds after midnight.
     */
    @DatabaseField(columnName = "departure_time", canBeNull = true)
    private int departureTime; // Seconds after midnight

    /**
     * Empty constructor required by OrmLite.
     */
    public StopTimeModel() {
    }

    /**
     * Constructs a StopTimeModel with the specified trip, stop, arrival time, and
     * departure time.
     *
     * @param trip           the trip associated with this stop time
     * @param stop           the stop associated with this stop time
     * @param arrival_time   arrival time in seconds after midnight
     * @param departure_time departure time in seconds after midnight
     */
    public StopTimeModel(TripModel trip, StopModel stop, int arrival_time, int departure_time) {
        this.trip = trip;
        this.stop = stop;
        this.arrivalTime = arrival_time;
        this.departureTime = departure_time;
    }

    /**
     * Gets the unique identifier for this stop time entry.
     *
     * @return the ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the stop associated with this stop time.
     *
     * @return the StopModel
     */
    public StopModel getStop() {
        return stop;
    }

    /**
     * Gets the trip associated with this stop time.
     *
     * @return the TripModel
     */
    public TripModel getTrip() {
        return trip;
    }

    /**
     * Gets the arrival time in seconds after midnight.
     *
     * @return the arrival time
     */
    public int getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Gets the arrival time as a formatted string.
     *
     * @return the arrival time string
     */
    public String getArrivalTimeAsStr() {
        return Utils.secondsOfDayToString(arrivalTime);
    }

    /**
     * Gets the departure time in seconds after midnight.
     *
     * @return the departure time
     */
    public int getDepartureTime() {
        return departureTime;
    }

    /**
     * Gets the departure time as a formatted string.
     *
     * @return the departure time string
     */
    public String getDepartureTimeAsStr() {
        return Utils.secondsOfDayToString(departureTime);
    }

    /**
     * Returns a string representation of the StopTimeModel.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format("Stop ID: %s, Trip ID: %s, Arrival Time: %s, Departure Time: %s",
                stop.getId(), trip.getId(), getArrivalTimeAsStr(), getDepartureTimeAsStr());
    }
}
