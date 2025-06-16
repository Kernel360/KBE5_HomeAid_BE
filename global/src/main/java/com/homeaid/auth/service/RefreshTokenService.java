package com.homeaid.auth.service;

import com.homeaid.util.RedisKeyFactory;
import com.homeaid.util.RedisUtil;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

  private final RedisUtil redisUtil;

  public RefreshTokenService(RedisUtil redisUtil) {
    this.redisUtil = redisUtil;
  }

  public void saveRefreshToken(String username, String refreshToken, long ttl, TimeUnit timeUnit) {
    String key = RedisKeyFactory.refreshTokenKey(username);
    redisUtil.save(key, refreshToken, ttl, timeUnit);
  }

  public String getRefreshToken(String username) {
    String key = RedisKeyFactory.refreshTokenKey(username);
    return redisUtil.getData(key);
  }

  public void deleteRefreshToken(String username) {
    String key = RedisKeyFactory.refreshTokenKey(username);
    redisUtil.delete(key);
  }
}
