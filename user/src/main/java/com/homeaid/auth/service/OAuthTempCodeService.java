package com.homeaid.auth.service;

import com.homeaid.auth.dto.response.TempOAuthUserInfo;
import com.homeaid.domain.User;
import java.util.Optional;

public interface OAuthTempCodeService {

  // 신규 OAuth 사용자 임시 정보 저장
  void storeNewUserInfo(String oauthCode, TempOAuthUserInfo tempUserInfo);

  TempOAuthUserInfo getNewUserInfo(String oauthCode);

  // 임시 코드 삭제
  boolean deleteOAuthCode(String oauthCode);

  // 기존 사용자 OAuth 코드 저장
  void storeExistingUserCode(String oauthCode, Long userId);

  // 기존 사용자 ID 조회
  User getExistingUserId(String oauthCode);
}