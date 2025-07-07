package com.homeaid.auth.service;

import com.homeaid.auth.user.CustomOAuth2User;
import com.homeaid.auth.user.GoogleUserDetails;
import com.homeaid.auth.user.OAuth2UserInfo;
import com.homeaid.domain.User;
import com.homeaid.domain.enumerate.UserRole;
import com.homeaid.repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = super.loadUser(userRequest);
    log.info("getAttributes() : {}", oauth2User.getAttributes());

    String provider = userRequest.getClientRegistration().getRegistrationId();

    OAuth2UserInfo oAuth2UserInfo = null;

    // 구글 외 다른 소셜 로그인 추가 시, 구분이 필요하기 때문
    if (provider.equals("google")) {
      log.info("구글 로그인 시도");
      oAuth2UserInfo = new GoogleUserDetails(oauth2User.getAttributes());
    }

    try {
      if (oAuth2UserInfo != null) {
        return processOAuth2User(userRequest, oauth2User, oAuth2UserInfo);
      }
    } catch (Exception e) {
      log.error("OAuth2 사용자 처리 중 오류 발생", e);
      throw new OAuth2AuthenticationException("OAuth2 사용자 처리 실패");
    }
    return oauth2User;
  }

  private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User,
      OAuth2UserInfo oAuth2UserInfo) {
    String registrationId = userRequest.getClientRegistration().getRegistrationId();

    Map<String, Object> attributes = oauth2User.getAttributes();
    String providerId = oAuth2UserInfo.getProviderId();

    User user = saveOrUpdateUser(oAuth2UserInfo, registrationId);

    return new CustomOAuth2User(user, attributes);
  }

  private User saveOrUpdateUser(OAuth2UserInfo userInfo, String registrationId) {
    Optional<User> existingUser = userRepository.findByEmail(userInfo.getEmail());

    if (existingUser.isPresent()) {
      User user = existingUser.get();

      if (!user.getProvider().equals(registrationId)) {
        throw new OAuth2AuthenticationException(
            "이미 " + user.getProvider() + " 계정으로 가입된 이메일입니다.");
      }

      user.updateOAuthProfile(userInfo.getName(), userInfo.getImageUrl());
      log.info("기존 사용자 정보 업데이트: {}", user.getEmail());
      return userRepository.save(user);

    } else {
      // 새 사용자 생성 (추가 정보 입력 필요)
      User newUser = User.createOAuth2User(
          registrationId,
          userInfo.getProviderId(),
          userInfo.getEmail(),
          userInfo.getName(),
          userInfo.getImageUrl(),
          UserRole.CUSTOMER
      );

      log.info("새 OAuth2 사용자 생성: {} (추가 정보 입력 필요)", newUser.getEmail());
      return userRepository.save(newUser);
    }
  }
}
