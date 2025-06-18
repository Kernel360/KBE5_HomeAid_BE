package com.homeaid.auth.service;

import com.homeaid.util.RedisKeyFactory;
import com.homeaid.util.RedisUtil;
import java.time.Duration;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

  private final RedisUtil redisUtil;


  public TokenBlacklistService(RedisUtil redisUtil) {
    this.redisUtil = redisUtil;
  }

  public void blacklistAccessToken(String accessToken, long expirationMillis) {
    String key = RedisKeyFactory.buildBlacklistTokenKey(accessToken);
    Duration duration = Duration.ofMillis(expirationMillis);
    redisUtil.save(key, "blacklist", duration);
  }

  public boolean isBlacklisted(String accessToken) {
    String key = RedisKeyFactory.buildBlacklistTokenKey(accessToken);
    return redisUtil.hasKey(key);
  }
}
