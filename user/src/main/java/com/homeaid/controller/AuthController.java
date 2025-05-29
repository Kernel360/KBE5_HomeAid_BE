package com.example.homeaid.global.auth.controller;

import com.example.homeaid.global.auth.dto.request.CustomerSignUpRequestDto;
import com.example.homeaid.global.auth.dto.request.ManagerSignUpRequestDto;
import com.example.homeaid.global.auth.service.AuthService;
import com.example.homeaid.manager.repository.ManagerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/signup/manager")
  public ResponseEntity<Void> signUpManager(
      @RequestBody @Valid ManagerSignUpRequestDto managerSignUpRequestDto
  ) {
     authService.registerManager(managerSignUpRequestDto);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/signup/customer")
  public ResponseEntity<Void> signUpCustomer(
      @RequestBody @Valid CustomerSignUpRequestDto customerSignUpRequestDto
  ){
    authService.registerCustomer(customerSignUpRequestDto);
    return ResponseEntity.ok().build();
  }

}
