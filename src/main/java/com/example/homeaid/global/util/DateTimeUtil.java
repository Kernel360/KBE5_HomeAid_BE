package com.example.homeaid.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static String formatToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}
