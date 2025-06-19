package com.homeaid.service;

import com.homeaid.common.enumerate.DocumentType;
import com.homeaid.common.request.UploadFileParam;
import com.homeaid.common.response.FileUploadResult;
import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.domain.ManagerDocument;
import com.homeaid.domain.User;
import com.homeaid.dto.request.SignInRequestDto;
import com.homeaid.dto.request.UserUpdateRequestDto;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ErrorCode;
import com.homeaid.exception.UserErrorCode;
import com.homeaid.repository.ManagerDocumentRepository;
import com.homeaid.repository.UserRepository;
import com.homeaid.security.jwt.JwtTokenProvider;
import com.homeaid.util.S3Service;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
  private final ManagerDocumentRepository managerDocumentRepository;
  private final S3Service s3Service;

  private static final String S3_PACKAGE_NAME_ID = "USERS/MANAGERS/ID_FILE/";
  private static final String S3_PACKAGE_NAME_CRIMINAL = "USERS/MANAGERS/CRIMINAL_FILE/";
  private static final String S3_PACKAGE_NAME_HEALTH = "USERS/MANAGERS/HEALTH_FILE/";

  public Manager signUpManager(@Valid Manager manager) {

    if (userRepository.existsByPhone(manager.getPhone())) {
      throw new CustomException(UserErrorCode.USER_ALREADY_EXISTS);
    }

    userRepository.save(manager);

    return manager;
  }

  @Override
  public Customer signUpCustomer(Customer customer) {

    if (userRepository.existsByPhone(customer.getPhone())) {
      throw new CustomException(UserErrorCode.USER_ALREADY_EXISTS);
    }

    userRepository.save(customer);
    return customer;
  }


  @Override
  public User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }

  public String loginAndGetToken(SignInRequestDto request) {
    var user = userRepository.findByPhone(request.getPhone());
    if (user.isEmpty()) {
      log.warn("Login failed - User not found: email={}", request.getPhone());
      throw new CustomException(UserErrorCode.LOGIN_FAILED);
    }

    if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
      log.warn("Login failed - Invalid password: email={}", request.getPhone());
      throw new CustomException(UserErrorCode.LOGIN_FAILED);
    }
    return jwtTokenProvider.createAccessToken(user.get().getId(), user.get().getRole().name());
  }

  @Transactional
  public void updateUserInfo(Long userId, UserUpdateRequestDto dto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

    user.updateInfo(dto.getName(), dto.getEmail(), dto.getPhone());
  }

  @Override
  public void uploadManagerFiles(Manager manager, MultipartFile idFile, MultipartFile criminalFile,
      MultipartFile healthFile)
      throws IOException {

    List<FileUploadResult> managerDocuments = Stream.of(
            new UploadFileParam(DocumentType.ID_CARD, S3_PACKAGE_NAME_ID, idFile),
            new UploadFileParam(DocumentType.CRIMINAL_RECORD, S3_PACKAGE_NAME_CRIMINAL, criminalFile),
            new UploadFileParam(DocumentType.HEALTH_CERTIFICATE, S3_PACKAGE_NAME_HEALTH, healthFile)
        ).filter(param -> !param.file().isEmpty())
        .map(param -> {
          try {
            return s3Service.uploadFile(param.documentType(), param.packageName(), param.file());
          } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
          }
        })
        .collect(Collectors.toList());

    List<ManagerDocument> documents = managerDocuments.stream()
        .map(meta -> ManagerDocument.builder()
            .documentType(meta.getDocumentType())
            .documentS3Key(meta.getS3Key())
            .documentUrl(meta.getUrl())
            .manager(manager)
            .build())
        .collect(Collectors.toList());

    managerDocumentRepository.saveAll(documents);
  }
}
