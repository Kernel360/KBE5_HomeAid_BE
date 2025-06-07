package com.homeaid.security;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
  private final JwtUtil jwtUtil;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager)
      throws Exception {
    SecurityAuthenticationFilter filter = new SecurityAuthenticationFilter(authManager, jwtUtil);
    filter.setFilterProcessesUrl("/api/v1/user/auth/signin");

    http
        .csrf((auth) -> auth.disable())
        .formLogin((auth) -> auth.disable())
        .cors((cors) -> cors.configurationSource(corsConfigurationSource()))
        .httpBasic((auth) -> auth.disable())
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http
        .authorizeHttpRequests((auth) -> auth
            .requestMatchers("/api/v1/user/auth/**", "/api/v1/swagger/auth/**").permitAll()
            .requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-resources",
                "/configuration/ui",
                "/configuration/security",
                "/webjars/**"
            ).permitAll()

            .requestMatchers("/api/v1/admin").hasRole("ADMIN")
            .requestMatchers("/api/v1/customer").hasRole("CUSTOMER")
            .requestMatchers("/api/v1/manager").hasRole("MANAGER")
            .anyRequest().authenticated()
        );

    // JwtAuthenticationFilter 추가
    http
        .addFilterBefore(new JwtFilter(jwtUtil, customUserDetailsService),
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
    // configuration.setAllowedOrigins(Arrays.asList("*")); // 모든 도메인 허용 (개발 환경에서만 사용)

    // 허용할 HTTP 메서드
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    // 허용할 헤더
    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization",
        "Content-Type",
        "X-Requested-With",
        "Accept",
        "Origin",
        "Access-Control-Request-Method",
        "Access-Control-Request-Headers"
    ));

    // 쿠키 허용
    configuration.setAllowCredentials(true);

    // preflight 요청의 캐시 시간 (초)
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

}
