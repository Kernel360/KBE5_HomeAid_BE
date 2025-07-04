package com.homeaid.service;

import com.homeaid.domain.User;
import com.homeaid.dto.request.UserUpdateRequestDto;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  User getUserById(Long id);

  void updateUserInfo(Long userId, UserUpdateRequestDto dto);

  void uploadProfileImage(Long userId, MultipartFile file) throws IOException;

  void deleteProfileImage(Long userId);
}
