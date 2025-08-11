package com.nnamo.enums;

public enum RouteType {
    TRAM("Tram"),
    METRO("Metro"),
    RAIL("Rail"),
    BUS("Bus"),
    FERRY("Ferry"),
    CABLE_TRAM("Cable Tram"),
    AERIAL_LIFT("Aerial Lift"),
    FUNICULAR("Funicular"),
    TROLLEYBUS("Trolleybus"),
    MONORAIL("Monorail");

    private String name;

    RouteType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
