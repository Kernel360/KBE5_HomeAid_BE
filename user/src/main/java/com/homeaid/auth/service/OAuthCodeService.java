package com.homeaid.auth.service;

import com.homeaid.domain.User;
import java.util.Optional;

public interface OAuthCodeService {
  Optional<User> resolve(String oauthCode);
  void store(String oauthCode, Long userId);
}