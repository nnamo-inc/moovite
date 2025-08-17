package com.nnamo.enums;

public enum VehicleType {
    BUS("Bus"),
    TRAM("Tram"),
    METRO("Metro"),
    TRENO("Treno"),
    COTRAL("Cotral");

    private String value;

    VehicleType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
