package com.nnamo.enums;

public enum ColumnName {
    ROUTENAME("Route name"),
    TERMINAL("Terminal"),
    DIRECTION("Direction"),
    TIME("Time"),
    STATE("State"),
    MINUTESLEFT("Minutes left"),
    AVAILABLESEATS("Available seats"),
    STOPNAME("Stop name"),
    CODE("Code"),
    TYPE("Type"),
    TRIP("Trip"),
    DELAY("Delay"),
    INFORMATION("Information");

    String name;

    ColumnName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
