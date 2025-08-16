package com.nnamo.utils;

import java.time.LocalTime;
import java.util.Date;

public class Utils {
    public static Date getCurrentDate() {
        return new Date();
    }

    public static LocalTime getCurrentTime() {
        return LocalTime.now();
    }
}
