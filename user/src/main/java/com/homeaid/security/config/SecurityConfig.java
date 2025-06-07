package com.homeaid.security.config;

import com.homeaid.security.RefreshTokenFilter;
import com.homeaid.security.user.CustomUserDetailsService;
import com.homeaid.security.filter.AccessTokenFilter;
import com.homeaid.security.filter.JwtAuthenticationFilter;
import com.homeaid.security.token.JwtTokenProvider;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;
  private final JwtTokenProvider jwtTokenProvider;
  private final String[] allowUrls = {"/", "/api/v1/users/signup/**", "/api/v1/swagger/users/**"};
  private final String[] swaggerUrls = {"/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-resources",
      "/configuration/ui", "/configuration/security", "/webjars/**"};



  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager)
      throws Exception {
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authManager,
        jwtTokenProvider);
    filter.setFilterProcessesUrl("/api/v1/auth/signin");

    http
        .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (JWT 사용)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .cors((cors) -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션 비활성화

    http
        .authorizeHttpRequests((auth) -> auth
            .requestMatchers(allowUrls).permitAll()
            .requestMatchers(swaggerUrls).permitAll()
//            .requestMatchers("/api/v1/admin").hasRole("ADMIN") // ex. 관리자 권한 가진 사용자만 접근 가능
//            .requestMatchers("/api/v1/customer").hasRole("CUSTOMER") // ex. 고객 권한 가진 사용자만 접근 가능
//            .requestMatchers("/api/v1/manager").hasRole("MANAGER") // ex. 매니저 권한 가진 사용자만 접근 가능
            .anyRequest().authenticated() // 나머지 모든 요청 인증 필요
        );

    // JwtAuthenticationFilter 추가
    http
        .addFilterBefore(new AccessTokenFilter(jwtTokenProvider, customUserDetailsService),
            UsernamePasswordAuthenticationFilter.class);

    // LoginFilter 추가
    http
        .addFilterAt(filter, UsernamePasswordAuthenticationFilter.class);

    return http.build();

  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // CORS 설정을 위한 Bean 추가
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // TODO 배포 후 https로 바꾸면 보안 설정 추가해야 함
    // 프론트엔드 도메인 허용
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // React 개발 서버
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization",
        "Content-Type",
        "X-Requested-With",
        "Accept",
        "Origin",
        "Access-Control-Request-Method",
        "Access-Control-Request-Headers"
    )); // 허용할 헤더

    configuration.setAllowCredentials(true); // 쿠키 허용
    configuration.setMaxAge(3600L); // preflight 요청의 캐시 시간 (초)

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

}
