package com.homeaid.auth.service;

import com.homeaid.util.RedisKeyFactory;
import com.homeaid.util.RedisUtil;
import java.time.Duration;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

  private final RedisUtil redisUtil;
  private static final Duration DURATION = Duration.ofDays(1);

  public RefreshTokenService(RedisUtil redisUtil) {
    this.redisUtil = redisUtil;
  }

  // key: PREFIX + userId
  public void saveRefreshToken(Long userId, String refreshToken) {
    String key = RedisKeyFactory.buildeRefreshTokenKey(userId);
    redisUtil.save(key, refreshToken, DURATION);
  }

  public String getRefreshToken(Long userId) {
    String key = RedisKeyFactory.buildeRefreshTokenKey(userId);
    return redisUtil.getData(key);
  }

  public void deleteRefreshToken(Long userId) {
    String key = RedisKeyFactory.buildeRefreshTokenKey(userId);
    redisUtil.delete(key);
  }

  public boolean isValidRefreshToken(Long userId, String refreshToken) {
    String saved = getRefreshToken(userId);
    return saved != null && saved.equals(refreshToken);
  }
}
