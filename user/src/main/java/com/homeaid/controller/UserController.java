package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.domain.User;
import com.homeaid.dto.request.CustomerSignUpRequestDto;
import com.homeaid.dto.request.ManagerSignUpRequestDto;
import com.homeaid.dto.request.UserUpdateRequestDto;
import com.homeaid.dto.response.UserProfileResponseDto;
import com.homeaid.dto.response.SignUpResponseDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ErrorCode;
import com.homeaid.security.user.CustomUserDetails;
import com.homeaid.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  @GetMapping("/my")
  @Operation(summary = "회원 기본 정보 조회", description = "회원정보 기본 정보 수정을 위한 회원 정보 조회")
  public ResponseEntity<CommonApiResponse<UserProfileResponseDto>> getUserProfile(
      @AuthenticationPrincipal CustomUserDetails user) {

    Long userId = user.getUserId();
    User userProfile = userService.getUserById(userId);

    log.debug("유저 프로필 이미지 URL: {}", userProfile.getProfileImageUrl());

    return ResponseEntity.status(HttpStatus.OK)
        .body(CommonApiResponse.success(UserProfileResponseDto.toUserProfileDto(userProfile)));
  }

  @PutMapping("/my")
  @Operation(summary = "회원 정보 수정")
  public ResponseEntity<Void> updateUserInfo(
      @AuthenticationPrincipal CustomUserDetails user,
      @Valid @RequestBody UserUpdateRequestDto dto) {

    Long userId = user.getUserId();
    userService.updateUserInfo(userId, dto);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(value = "/my/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "회원 프로필 이미지 등록")
  public ResponseEntity<CommonApiResponse<Void>> updateProfileImage(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestPart MultipartFile file
  ) throws IOException {

    if (file.isEmpty()) {
      throw new CustomException(ErrorCode.FILE_EMPTY);
    }

    Long userId = user.getUserId();
    userService.uploadProfileImage(userId, file);

    return ResponseEntity.status(HttpStatus.CREATED).body(CommonApiResponse.success());
  }

  @DeleteMapping(value = "/my/image")
  @Operation(summary = "회원 프로필 이미지 삭제")
  public ResponseEntity<CommonApiResponse<Void>> deleteProfileImage(
      @AuthenticationPrincipal CustomUserDetails user
  ) throws IOException {

    Long userId = user.getUserId();
    userService.deleteProfileImage(userId);

    return ResponseEntity.status(HttpStatus.OK).body(CommonApiResponse.success());
  }
}
