package com.homeaid.util;

public class RedisKeyFactory {

  // RefreshToken 키 생성
  public static String buildRefreshTokenKey(Long userId) {
    return "RefreshToken:" + userId;
  }

  // BlackList 키 생성
  public static String buildBlacklistTokenKey(String accessToken) {
    return "BlackListToken:" + accessToken;
  }

  public static String buildAdminStatisticsKey(int year, Integer month, Integer day) {
    StringBuilder sb = new StringBuilder("admin:statistics:").append(year);
    if (month != null) sb.append(":").append(String.format("%02d", month));
    if (day != null) sb.append(":").append(String.format("%02d", day));
    return sb.toString();
  }

  // 신규 OAuth 사용자 임시 정보 키 생성
  public static String buildOAuthNewUserKey(String oauthCode) {
    return "OAUTH_NEW_USER: " + oauthCode;
  }

   // 기존 OAuth 사용자 임시 코드 키 생성
  public static String buildOAuthCodeKey(String oauthCode) {
    return "OAUTH_CODE: " + oauthCode;
  }
}
