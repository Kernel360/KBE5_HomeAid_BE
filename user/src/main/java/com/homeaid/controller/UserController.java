package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.dto.request.CustomerSignUpRequestDto;
import com.homeaid.dto.request.ManagerSignUpRequestDto;
import com.homeaid.dto.request.UserUpdateRequestDto;
import com.homeaid.dto.response.SignUpResponseDto;
import com.homeaid.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;
  private final BCryptPasswordEncoder passwordEncoder;

  @PostMapping("/signup/managers")
  public ResponseEntity<CommonApiResponse<SignUpResponseDto>> signUpManager(
      @RequestBody @Valid ManagerSignUpRequestDto managerSignUpRequestDto
  ) {

    String password = passwordEncoder.encode(managerSignUpRequestDto.getPassword());
    Manager manager = userService.signUpManager(
        ManagerSignUpRequestDto.toEntity(managerSignUpRequestDto, password) // 비밀번호 추가 수정 필요
    );
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(SignUpResponseDto.toManagerDto(manager)));
  }

  @PostMapping("/signup/customers")
  public ResponseEntity<CommonApiResponse<SignUpResponseDto>> signUpCustomer(
      @RequestBody @Valid CustomerSignUpRequestDto customerSignUpRequestDto
  ) {

    String password = passwordEncoder.encode(customerSignUpRequestDto.getPassword());

    Customer customer = userService.signUpCustomer(
        CustomerSignUpRequestDto.toEntity(customerSignUpRequestDto, password) // 비밀번호 추가 수정 필요
    );
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(SignUpResponseDto.toCustomerDto(customer)));
  }

  @PutMapping("/{userId}")
  @Operation(summary = "회원 정보 수정")
  public ResponseEntity<Void> updateUserInfo(
      @PathVariable Long userId,
      @Valid @RequestBody UserUpdateRequestDto dto) {

    userService.updateUserInfo(userId, dto);
    return ResponseEntity.noContent().build();
  }

}
