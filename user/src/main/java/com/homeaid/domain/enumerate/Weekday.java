package com.homeaid.domain.enumerate;

import java.time.LocalDate;

public enum Weekday {
  MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

  public static Weekday from(LocalDate date) {
    return Weekday.valueOf(date.getDayOfWeek().name());  // ex. MONDAY
  }
}
