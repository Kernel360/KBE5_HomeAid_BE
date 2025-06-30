package com.homeaid.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // LocalDateTime → "2025-12-13 12:24"
    public static String formatToSimpleDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(FORMATTER);
    }
}
