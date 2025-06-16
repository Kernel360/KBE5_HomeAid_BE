package com.homeaid.util;

public class RedisKeyFactory {

  public static String buildeRefreshTokenKey(Long userId) {
    return "RT:" + userId;
  }

  public static String buildBlacklistTokenKey(String accessToken) {
    return "BL:" + accessToken;
  }
}
