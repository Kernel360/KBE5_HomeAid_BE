package com.homeaid.service;

import com.homeaid.common.enumerate.DocumentType;
import com.homeaid.common.response.FileUploadResult;
import com.homeaid.domain.User;
import com.homeaid.dto.request.UserUpdateRequestDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.UserRepository;
import com.homeaid.util.FileValidator;
import com.homeaid.util.S3Service;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final S3Service s3Service;

  private static final String PACKAGE_NAME = "USERS/PROFILE/";

  // 사용자 정보 수정
  @Transactional
  public void updateUserInfo(Long userId, UserUpdateRequestDto dto) {
    User user = getUserById(userId);

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
}
