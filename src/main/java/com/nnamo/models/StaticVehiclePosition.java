package com.nnamo.models;

import org.jxmapviewer.viewer.GeoPosition;

public class StaticVehiclePosition {
    private double latitude;
    private double longitude;

    public StaticVehiclePosition(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public StaticVehiclePosition(GeoPosition position) {
        this.latitude = position.getLatitude();
        this.longitude = position.getLongitude();
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public GeoPosition getPosition() {
        return new GeoPosition(this.latitude, this.longitude);
    }
}
