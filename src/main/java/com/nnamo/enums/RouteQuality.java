package com.nnamo.enums;

public enum RouteQuality {
    OFTEN_ON_TIME("Usually on time"),
    OFTEN_EARLY("Usually early"),
    OFTEN_DELAYED("Usually delayed"),
    INVALID("Couldn't process data"),
    NOT_ENOUGH_DATA("Not enough data");

    String text;

    RouteQuality(String text) {
        this.text = text;
    }

    public String toString() {
        return this.text;
    }
}
