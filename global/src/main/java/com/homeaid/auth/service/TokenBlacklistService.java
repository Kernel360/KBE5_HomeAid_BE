package com.homeaid.auth.service;

import com.homeaid.util.RedisKeyFactory;
import com.homeaid.util.RedisUtil;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

  private final RedisUtil redisUtil;

  public TokenBlacklistService(RedisUtil redisUtil) {
    this.redisUtil = redisUtil;
  }

  public void blacklistAccessToken(String accessToken, long ttl, TimeUnit timeUnit) {
    String key = RedisKeyFactory.blacklistTokenKey(accessToken);
    redisUtil.save(key, "blacklist", ttl, timeUnit);
  }

  public boolean isBlacklisted(String accessToken) {
    String key = RedisKeyFactory.blacklistTokenKey(accessToken);
    return redisUtil.hasKey(key);
  }
}
