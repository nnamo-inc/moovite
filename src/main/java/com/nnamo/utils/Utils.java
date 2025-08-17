package com.nnamo.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class Utils {
    public static Date getCurrentDate() {
        return new Date();
    }

    public static LocalTime getCurrentTime() {
        return LocalTime.now();
    }

    public static int posixToSecondsOfDay(long posixTime) {
        LocalDateTime dateTime = Instant
                .ofEpochSecond(posixTime, 0)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return dateTime.toLocalTime().toSecondOfDay();
    }

    public static String secondsOfDayToString(int seconds) {
        return String.format("%02d:%02d", seconds / 3600, (seconds % 3600) / 60);
    }
}
