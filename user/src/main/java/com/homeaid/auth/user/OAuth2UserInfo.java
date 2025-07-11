package com.homeaid.auth.user;

public interface OAuth2UserInfo {
  String getProvider();
  String getProviderId();
  String getImageUrl();
  String getEmail();
  String getName();
}
