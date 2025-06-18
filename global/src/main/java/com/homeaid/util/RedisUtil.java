package com.homeaid.util;

import io.lettuce.core.RedisConnectionException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisUtil {

  private final RedisTemplate<String, Object> redisTemplate;

  public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  // 데이터 저장
  public void save(String key, Object val, Duration timeout) {
    try {
      redisTemplate.opsForValue().set(key, val, timeout);
      log.debug("Redis 저장 성공 - Key: {}", key);
    } catch (RedisConnectionFailureException | RedisConnectionException e) {
      throw new RuntimeException("Redis 연결 실패", e);
    } catch (Exception e) {
      log.error("Redis 저장 실패 - Key: {}", key, e);
      throw new RuntimeException("Redis 저장 실패", e);
    }
  }

  // 데이터 조회(문자열)
  public String getData(String key) {
    try {
      Object value = redisTemplate.opsForValue().get(key);
      return value != null ? value.toString() : null;
    } catch (Exception e) {
      log.error("Redis 조회 실패 - Key: {}", key, e);
      return null;
    }
  }

  // 데이터 조회 (객체)
  public Object getObject(String key) {
    try {
      return redisTemplate.opsForValue().get(key);
    } catch (Exception e) {
      log.error("Redis 객체 조회 실패 - Key: {}", key, e);
      return null;
    }
  }

  // 데이터 삭제
  public boolean delete(String key) {
    try {
      Boolean result = redisTemplate.delete(key);
      boolean deleted = Boolean.TRUE.equals(result);
      log.debug("Redis 삭제 - Key: {}, 성공: {}", key, deleted);
      return deleted;
    } catch (Exception e) {
      log.error("Redis 삭제 실패 - Key: {}", key, e);
      return false;
    }
  }

  // 키 존재 확인
  public boolean hasKey(String key) {
    try {
      return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    } catch (Exception e) {
      log.error("Redis 키 존재 확인 실패 - Key: {}", key, e);
      return false;
    }
  }
}
