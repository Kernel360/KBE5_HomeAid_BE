package com.homeaid.auth.service;

import com.homeaid.domain.User;
import com.homeaid.repository.UserRepository;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthCodeServiceImpl implements OAuthCodeService {

  private static final String PREFIX = "OAUTH_CODE:";
  private final RedisTemplate<String, String> redisTemplate;
  private final UserRepository userRepository;

  @Override
  public Optional<User> resolve(String oauthCode) {
    String userIdStr = redisTemplate.opsForValue().getAndDelete(PREFIX + oauthCode);
    log.debug("임시토큰 resolve() - code={}, userIdStr={}", oauthCode, userIdStr);
    if (userIdStr == null) {
      return Optional.empty();
    }

    try {
      Long userId = Long.valueOf(userIdStr);
      Optional<User> user = userRepository.findById(userId);
      user.ifPresent(u -> log.debug("임시토큰 사용자 조회 성공 - id={}, email={}", u.getId(), u.getEmail()));
      return user;
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  @Override
  public void store(String oauthCode, Long userId) {
    redisTemplate.opsForValue().set(PREFIX + oauthCode, String.valueOf(userId), Duration.ofMinutes(5));
    log.debug("임시토큰 store() - 저장된 code={}, userId={}", oauthCode, userId);
  }
}