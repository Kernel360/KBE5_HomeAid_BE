package com.homeaid.util;

public class RedisKeyFactory {

  public static String buildeRefreshTokenKey(Long userId) {
    return "RT:" + userId;
  }

  public static String buildBlacklistTokenKey(String accessToken) {
    return "BL:" + accessToken;
  }

  public static String buildAdminStatisticsKey(int year, Integer month, Integer day) {
    StringBuilder sb = new StringBuilder("admin:statistics:").append(year);
    if (month != null) sb.append(":").append(String.format("%02d", month));
    if (day != null) sb.append(":").append(String.format("%02d", day));
    return sb.toString();
  }
}
