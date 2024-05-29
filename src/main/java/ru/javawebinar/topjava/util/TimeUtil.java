package ru.javawebinar.topjava.util;

import java.time.LocalTime;

public class TimeUtil {

    public static boolean isBetweenHalfOpen(LocalTime localTime, LocalTime startTime, LocalTime endTime) {
        return localTime.compareTo(startTime) >= 0 && localTime.compareTo(endTime) < 0;
    }
}
