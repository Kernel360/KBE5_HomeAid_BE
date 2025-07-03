package com.homeaid.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI springOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("HomeAid API 명세서")
            .version("v1")
            .description("HomeAid는 매칭시스템을 활용하여 고객에게 맞춤형 매니저를 매칭시켜주고 가사 및 청소 서비스를 제공합니다."))
        // 전역 보안 요구사항 추가
        .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
        // 보안 스키마 정의
        .components(new Components()
            .addSecuritySchemes("Bearer Authentication", createBearerTokenScheme()));
  }

  /**
   * Bearer 토큰 인증 스키마 생성
   */
  private SecurityScheme createBearerTokenScheme() {
    return new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .in(SecurityScheme.In.HEADER)
        .name("Authorization")
        .description("JWT 토큰을 입력하세요. 'Bearer' 키워드는 자동으로 추가됩니다.");
  }

  @Bean
  public GroupedOpenApi reservationAPI() {
    return GroupedOpenApi.builder()
        .group("reservations")
        .displayName("예약")
        .pathsToMatch("/api/v1/reservations/**")
        .build();
  }

  @Bean
  public GroupedOpenApi serviceOptionAPI() {
    return GroupedOpenApi.builder()
        .group("serviceOptions")
        .displayName("서비스 옵션")
        .pathsToMatch("/api/v1/admin/service-option/**")
        .build();
  }

  @Bean
  public GroupedOpenApi matchingAPI() {
    return GroupedOpenApi.builder()
        .group("matchings")
        .displayName("매칭")
        .pathsToMatch("/api/v1/*/matchings/**")
        .build();
  }

  @Bean
  public GroupedOpenApi userAPI() {
    return GroupedOpenApi.builder()
        .group("users")
        .displayName("로그인/회원가입")
        .pathsToMatch("/api/v1/swagger/users/**")
        .build();
  }

  @Bean
  public GroupedOpenApi worklogAPI() {
    return GroupedOpenApi.builder()
            .group("workLogs")
            .displayName("작업기록")
            .pathsToMatch("/api/v1/managers/work-logs/**")
            .build();
  }

  @Bean
  public GroupedOpenApi boardAPI() {
    return GroupedOpenApi.builder()
        .group("Boards")
        .displayName("문의글")
        .pathsToMatch("/api/v1/boards/**")
        .build();

  }

  @Bean
  public GroupedOpenApi reviewAPI() {
    return GroupedOpenApi.builder()
        .group("reviews")
        .displayName("리뷰")
        .pathsToMatch("/api/v1/reviews/**")
        .build();
  }

  @Bean
  public GroupedOpenApi paymentAPI() {
    return GroupedOpenApi.builder()
        .group("Payments")
        .displayName("결제")
        .pathsToMatch("/api/v1/my/payments/**")
        .build();
  }

  @Bean
  public GroupedOpenApi settlementAPI() {
    return GroupedOpenApi.builder()
        .group("정산")
        .pathsToMatch("/api/v1/my/settlement/**")
        .build();
  }

  @Bean
  public GroupedOpenApi managerAPI() {
    return GroupedOpenApi.builder()
        .group("매니저")
        .pathsToMatch("/api/v1/managers/**", "/api/v1/manager/profile/**")
        .build();
  }

  @Bean
  public GroupedOpenApi customerAPI() {
    return GroupedOpenApi.builder()
        .group("고객")
        .pathsToMatch("/api/v1/customers/**")
        .build();
  }

  @Bean
  public GroupedOpenApi adminAPI() {
    return GroupedOpenApi.builder()
        .group("admin")
        .displayName("관리자")
        .pathsToMatch("/api/v1/admin/**")
        .build();
  }
  
  @Bean
  public GroupedOpenApi withdrawalAPI() {
    return GroupedOpenApi.builder()
        .group("withdrawals")
        .displayName("회원 탈퇴")
        .pathsToMatch("/api/v1/my/withdrawal/**")
        .build();
  }
}