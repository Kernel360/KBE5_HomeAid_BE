package com.homeaid.util;

public class RedisKeyFactory {

  public static String refreshTokenKey(String username) {
    return "RT:" + username;
  }

  public static String blacklistTokenKey(String accessToken) {
    return "BL:" + accessToken;
  }
}
