package com.homeaid.auth.service;

import com.homeaid.auth.dto.response.TempOAuthUserInfo;
import com.homeaid.domain.User;
import com.homeaid.service.UserService;
import com.homeaid.util.RedisKeyFactory;
import com.homeaid.util.RedisUtil;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthTempCodeServiceImpl implements OAuthTempCodeService {

  private static final Duration OAUTH_CODE_EXPIRY = Duration.ofMinutes(10);
  private final RedisUtil redisUtil;
  private final UserService userService;

  // 기존 사용자 OAuth 코드 저장
  @Override
  public void storeExistingUserCode(String oauthCode, Long userId) {
    try {
      String key = RedisKeyFactory.buildOAuthCodeKey(oauthCode);
      redisUtil.save(key, userId, OAUTH_CODE_EXPIRY);
      log.info("기존 사용자 OAuth 코드 저장 완료 - UserId: {}, Code: {}", userId, oauthCode);
    } catch (Exception e) {
      log.error("기존 사용자 OAuth 코드 저장 실패 - UserId: {}, Code: {}", userId, oauthCode, e);
      throw new RuntimeException("OAuth 코드 저장에 실패했습니다.", e);
    }
  }

  // 신규 OAuth 사용자 임시 정보 저장
  @Override
  public void storeNewUserInfo(String oauthCode, TempOAuthUserInfo tempUserInfo) {
    try {
      String key = RedisKeyFactory.buildOAuthNewUserKey(oauthCode);
      redisUtil.save(key, tempUserInfo, OAUTH_CODE_EXPIRY);
      log.info("신규 사용자 OAuth 임시 정보 저장 완료 - Email: {}, Code: {}",
          tempUserInfo.getEmail(), oauthCode);
    } catch (Exception e) {
      log.error("신규 사용자 OAuth 임시 정보 저장 실패 - Email: {}, Code: {}",
          tempUserInfo.getEmail(), oauthCode, e);
      throw new RuntimeException("OAuth 임시 정보 저장에 실패했습니다.", e);
    }
  }

  // 신규 사용자 임시 정보 조회
  @Override
  public TempOAuthUserInfo getNewUserInfo(String oauthCode) {
    try {
      String key = RedisKeyFactory.buildOAuthNewUserKey(oauthCode);
      Object userInfo = redisUtil.getObject(key);

      if (userInfo instanceof TempOAuthUserInfo) {
        log.info("신규 사용자 임시 정보 조회 성공 - Code: {}", oauthCode);
        return (TempOAuthUserInfo) userInfo;
      }

      log.warn("신규 사용자 임시 정보 조회 실패 - Code: {}, 데이터 없음", oauthCode);
      return null;
    } catch (Exception e) {
      log.error("신규 사용자 임시 정보 조회 실패 - Code: {}", oauthCode, e);
      return null;
    }
  }

  // 임시 코드 삭제
  @Override
  public boolean deleteOAuthCode(String oauthCode) {
    try {
      String existingUserKey = RedisKeyFactory.buildOAuthCodeKey(oauthCode);
      String newUserKey = RedisKeyFactory.buildOAuthNewUserKey(oauthCode);

      boolean deleted1 = redisUtil.delete(existingUserKey);
      boolean deleted2 = redisUtil.delete(newUserKey);

      boolean result = deleted1 || deleted2;
      log.info("OAuth 코드 삭제 - Code: {}, 성공: {}", oauthCode, result);
      return result;
    } catch (Exception e) {
      log.error("OAuth 코드 삭제 실패 - Code: {}", oauthCode, e);
      return false;
    }
  }

  // 기존 사용자 ID 조회
  @Override
  public User getExistingUserId(String oauthCode) {
      try {
        String key = RedisKeyFactory.buildOAuthCodeKey(oauthCode);
        Object userId = redisUtil.getObject(key);

        Long longUserId;
        if (userId instanceof Integer) {
          longUserId = ((Integer) userId).longValue();
        } else if (userId instanceof Long) {
          longUserId = (Long) userId;
        } else {
          throw new IllegalStateException("Unexpected userId type in Redis: " + userId.getClass());
        }

        return userService.getUserById(longUserId);
      } catch (Exception e) {
        log.error("기존 사용자 ID 조회 실패 - Code: {}", oauthCode, e);

        throw new RuntimeException(e);
      }
  }
}