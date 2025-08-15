package com.nnamo.enums;

public enum RealtimeMetricType {
    TOTAL_BUS(0),
    STOPPED_BUS(1),
    LATE_BUS(2),
    EARLY_BUS(3),
    PUNCTUAL_BUS(4),
    DETOUR_BUS(5);

    private final int value;

    RealtimeMetricType(int value) {
        this.value = value;
    }
}
