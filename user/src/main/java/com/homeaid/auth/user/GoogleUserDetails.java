package com.homeaid.auth.user;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public class GoogleUserDetails implements OAuth2UserInfo { // OAuth 공급자(Google)로부터 받은 attribute 에서 필요한 값만 꺼내주는 클래스

  private Map<String, Object> attributes;

  @Override
  public String getProvider() {
    return "google";
  }

  @Override
  public String getProviderId() {
    return getStringAttribute("sub"); // Google에서 제공하는 고유 식별자 id = sub
  }

  @Override
  public String getImageUrl() {
    return getStringAttribute("picture");
  }

  @Override
  public String getEmail() {
    return getStringAttribute("email");
  }

  @Override
  public String getName() {
    return getStringAttribute("name");
  }

  private String getStringAttribute(String key) {
    Object value = attributes.get(key);
    if (value == null) {
      log.warn("OAuth2 응답에 '{}' 필드가 존재하지 않습니다.", key);
      return "";
    }
    return value.toString();
  }
}
