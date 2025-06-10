package com.homeaid.controller;

import com.homeaid.common.response.CommonApiResponse;
import com.homeaid.domain.Customer;
import com.homeaid.domain.Manager;
import com.homeaid.dto.request.CustomerSignUpRequestDto;
import com.homeaid.dto.request.ManagerSignUpRequestDto;
import com.homeaid.dto.request.SignInRequestDto;
import com.homeaid.dto.response.SignUpResponseDto;
import com.homeaid.dto.response.SignInResponseDto;
import com.homeaid.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/swagger/users")
@RequiredArgsConstructor
@Tag(name = "로그인/회원가입", description = "사용자 로그인/회원가입 API (매니저/고객)")
//@Profile("swagger")  // swagger 프로파일에서만 활성화 가능 (선택)
public class SwaggerAuthController {

  private final UserServiceImpl userServiceImpl;
  private final BCryptPasswordEncoder passwordEncoder;

  @PostMapping("/signup/managers")
  @Operation(summary = "매니저 회원가입", description = "매니저 사용자 정보를 입력받아 회원가입을 처리합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "회원가입 성공"
          , content = @Content(schema = @Schema(implementation = SignUpResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 입력값"
          , content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일"
          , content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  public ResponseEntity<CommonApiResponse<SignUpResponseDto>> signUpManager(
      @RequestBody @Valid ManagerSignUpRequestDto managerSignUpRequestDto
  ) {

    String password = passwordEncoder.encode(managerSignUpRequestDto.getPassword());
    Manager manager = userServiceImpl.signUpManager(
        ManagerSignUpRequestDto.toEntity(managerSignUpRequestDto, password) // 비밀번호 추가 수정 필요
    );
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(SignUpResponseDto.toManagerDto(manager)));
  }

  @Operation(summary = "고객 회원가입", description = "고객 사용자 정보를 입력받아 회원가입을 처리합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "회원가입 성공"
          , content = @Content(schema = @Schema(implementation = SignUpResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 입력값"
          , content = @Content(schema = @Schema(implementation = CommonApiResponse.class))),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일"
          , content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  @PostMapping("/signup/customers")
  public ResponseEntity<CommonApiResponse<SignUpResponseDto>> signUpCustomer(
      @RequestBody @Valid CustomerSignUpRequestDto customerSignUpRequestDto
  ) {

    String password = passwordEncoder.encode(customerSignUpRequestDto.getPassword());

    Customer customer = userServiceImpl.signUpCustomer(
        CustomerSignUpRequestDto.toEntity(customerSignUpRequestDto, password) // 비밀번호 추가 수정 필요
    );
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(SignUpResponseDto.toCustomerDto(customer)));
  }

  @Operation(summary = "로그인", description = "이메일과 비밀번호를 받아 로그인을 처리합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그인 성공"
          , content = @Content(schema = @Schema(implementation = SignUpResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 입력값"
          , content = @Content(schema = @Schema(implementation = CommonApiResponse.class)))
  })
  @PostMapping("/signin")
  public ResponseEntity<SignInResponseDto> swaggerSignIn(
      @RequestBody SignInRequestDto swaggerRequest) {
    String token = userServiceImpl.loginAndGetToken(swaggerRequest);
    return ResponseEntity.ok(new SignInResponseDto(token));
  }

}
