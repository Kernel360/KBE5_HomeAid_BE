package com.homeaid.service;

import com.homeaid.common.enumerate.DocumentType;
import com.homeaid.common.response.FileUploadResult;
import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.domain.User;
import com.homeaid.dto.request.SignInRequestDto;
import com.homeaid.dto.request.UserUpdateRequestDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.UserRepository;
import com.homeaid.security.jwt.JwtTokenProvider;
import com.homeaid.util.FileValidator;
import com.homeaid.util.S3Service;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final S3Service s3Service;

  private static final String PACKAGE_NAME = "USERS/PROFILE/";

  // 매니저 회원가입
  public Manager signUpManager(@Valid Manager manager) {

    if (userRepository.existsByPhone(manager.getPhone())) {
      log.warn("[회원가입 실패] 이미 존재하는 전화번호 - phone={}", manager.getPhone());
      throw new CustomException(UserErrorCode.USER_ALREADY_EXISTS);
    }

    userRepository.save(manager);
    log.info("[매니저 회원가입 완료] id={}, phone={}", manager.getId(), manager.getPhone());
    return manager;
  }

  // 고객 회원가입
  @Override
  public Customer signUpCustomer(Customer customer) {

    if (userRepository.existsByPhone(customer.getPhone())) {
      log.warn("[회원가입 실패] 이미 존재하는 전화번호 - phone={}", customer.getPhone());
      throw new CustomException(UserErrorCode.USER_ALREADY_EXISTS);
    }

    userRepository.save(customer);
    log.info("[고객 회원가입 완료] id={}, phone={}", customer.getId(), customer.getPhone());
    return customer;
  }

  // 사용자 정보 수정
  @Transactional
  public void updateUserInfo(Long userId, UserUpdateRequestDto dto) {
    User user =getUserById(userId);

    user.updateInfo(dto.getName(), dto.getEmail(), dto.getPhone());
  }

  // 프로필 이미지 등록
  @Override
  @Transactional
  public void uploadProfileImage(Long userId, MultipartFile file) throws IOException {
    User user = getUserById(userId);

    FileValidator.validateImageFile(file);

    String existingS3Key = user.getProfileImageS3Key();

    // 기존 이미지가 있다면 S3에서 삭제
    if (existingS3Key != null && !existingS3Key.isBlank()) {
      deleteProfileImage(userId);
    }

    // S3에 새 이미지 업로드
    FileUploadResult fileUploadResult = s3Service.uploadFile(DocumentType.PROFILE_IMAGE,
        PACKAGE_NAME, file);

    user.profileImage(fileUploadResult.getUrl(), fileUploadResult.getS3Key());
    userRepository.save(user);
  }

  // 프로필 이미지 삭제
  @Override
  @Transactional
  public void deleteProfileImage(Long userId) {
    User user = getUserById(userId);

    String profileImageS3Key = user.getProfileImageS3Key();

    // 이미지가 없으면 아무 동작 없이 종료
    if (profileImageS3Key == null || profileImageS3Key.isBlank()) {
      return;
    }

    try {
      s3Service.deleteFile(profileImageS3Key);
    } catch (Exception e) {
      log.error("파일 삭제 실패 - key: {}", profileImageS3Key, e);
      // 필요 시 예외 처리 로직 추가
    }

    user.profileImage(null, null);
    userRepository.save(user);
  }


  @Override
  public User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }


  // 스웨거 로그인 관련 메서드
  public String loginAndGetToken(SignInRequestDto request) {
    var user = userRepository.findByPhone(request.getPhone());
    if (user.isEmpty()) {
      log.warn("로그인 실패 - User not found: email={}", request.getPhone());
      throw new CustomException(UserErrorCode.LOGIN_FAILED);
    }

    if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
      log.warn("로그인 실패 - Invalid password: email={}", request.getPhone());
      throw new CustomException(UserErrorCode.LOGIN_FAILED);
    }
    return jwtTokenProvider.createAccessToken(user.get().getId(), user.get().getRole().name());
  }

}
