package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.dto.request.CustomerSignUpRequestDto;
import com.homeaid.dto.request.ManagerSignUpRequestDto;
import com.homeaid.dto.response.UserSignUpResponseDto;
import com.homeaid.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "SignUp/SignIn")
public class UserController {

  private final UserService userService;

  @PostMapping("/signup/manager")
  public ResponseEntity<CommonApiResponse<UserSignUpResponseDto>> signUpManager(
      @RequestBody @Valid ManagerSignUpRequestDto managerSignUpRequestDto
  ) {
    Manager manager = userService.signUpManager(
        ManagerSignUpRequestDto.toEntity(managerSignUpRequestDto) // 비밀번호 추가 수정 필요
    );
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(UserSignUpResponseDto.toManagerDto(manager)));
  }

  @PostMapping("/signup/customer")
  public ResponseEntity<CommonApiResponse<UserSignUpResponseDto>> signUpCustomer(
      @RequestBody @Valid CustomerSignUpRequestDto customerSignUpRequestDto
  ) {
    Customer customer = userService.signUpCustomer(
        CustomerSignUpRequestDto.toEntity(customerSignUpRequestDto) // 비밀번호 추가 수정 필요
    );
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(UserSignUpResponseDto.toCustomerDto(customer)));
  }

}
