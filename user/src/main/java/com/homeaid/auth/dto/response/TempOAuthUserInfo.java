package com.homeaid.auth.dto.response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TempOAuthUserInfo implements Serializable {

  private static final long serialVersionUID = 1L; // 직렬화 관련 경고 방지를 위한 직렬화 버전 ID 명시

  //OAuth 인증 성공 직후, 추가 정보 입력 전까지 임시로 Redis에 저장되는 사용자 정보
  private String provider;
  private String providerId;
  private String email;
  private String name;
  private String profileImageUrl;
  private String oauthCode; // 임시 토큰
  private long expireTime; // 만료 시간

  public static TempOAuthUserInfo create(String provider, String providerId, String email,
      String name, String profileImageUrl, String oauthCode) {
    return new TempOAuthUserInfo(
        provider,
        providerId,
        email,
        name,
        profileImageUrl,
        oauthCode,
        System.currentTimeMillis() + (10 * 60 * 1000) // 10분 후 만료
    );
  }
}
