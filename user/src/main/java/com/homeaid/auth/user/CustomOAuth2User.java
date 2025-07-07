package com.homeaid.auth.user;

import com.homeaid.domain.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

  private final User user;
  private final Map<String, Object> attributes; // 구글 등 OAuth에서 받은 사용자 정보

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
  }

  @Override
  public String getName() {
    return null;
  }

  public Long getUserId() {
    return this.user.getId();
  }

}
