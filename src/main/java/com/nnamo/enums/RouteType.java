package com.nnamo.enums;

public enum RouteType {
    ALL("All"),
    BUS("Bus"),
    TRAM("Tram"),
    METRO("Metro");

    private final String value;

    RouteType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RouteType fromString(String value) {
        for (RouteType type : RouteType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }

    @Override
    public String toString() {
        return this.value;
    }

}
