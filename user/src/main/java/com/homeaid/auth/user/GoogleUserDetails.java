package com.homeaid.auth.user;

import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GoogleUserDetails implements OAuth2UserInfo {

  private Map<String, Object> attributes;

  @Override
  public String getProvider() {
    return "google";
  }

  @Override
  public String getProviderId() {
    return attributes.get("sub").toString(); // Google에서 제공하는 고유 식별자 id = sub
  }

  @Override
  public String getImageUrl() {
    return attributes.get("picture").toString();
  }

  @Override
  public String getEmail() {
    return attributes.get("email").toString();
  }

  @Override
  public String getName() {
    return attributes.get("name").toString();
  }
}
