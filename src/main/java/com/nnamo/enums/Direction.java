package com.nnamo.enums;

public enum Direction {
    OUTBOUND, // 0 in GTFS
    INBOUND; // 1 in GTFS

    public static Direction getDirection(int directionIndex) {
        switch (directionIndex) {
            case 0:
                return OUTBOUND;
            case 1:
            default:
                return INBOUND;
        }
    }
}
