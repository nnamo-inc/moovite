package com.nnamo.enums;

public enum VehicleType {
    BUS("Bus"),
    TRAM("Tram"),
    METRO("Metro");

    private String value;

    VehicleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
