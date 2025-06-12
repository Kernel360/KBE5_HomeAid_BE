package com.homeaid.security.config;

import com.homeaid.security.filter.RefreshTokenFilter;
import com.homeaid.security.user.CustomUserDetailsService;
import com.homeaid.security.filter.AccessTokenFilter;
import com.homeaid.security.filter.JwtAuthenticationFilter;
import com.homeaid.security.token.JwtTokenProvider;
import java.util.Arrays;
import static org.springframework.security.config.Customizer.withDefaults;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
  private final RefreshTokenFilter refreshTokenFilter;

  private final String[] allowUrls = {"/", "/actuator/health",  "/api/v1/users/signup/**", "/api/v1/swagger/users/**", "/api/v1/auth/**"};
  private final String[] swaggerUrls = {"/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-resources",
      "/configuration/ui", "/configuration/security", "/webjars/**"};


  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager)
      throws Exception {
    JwtAuthenticationFilter signinFilter = new JwtAuthenticationFilter(authManager,
        jwtTokenProvider);
    signinFilter.setFilterProcessesUrl("/api/v1/auth/signin");

    http
        .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (JWT 사용)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .cors((cors) -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http
        .authorizeHttpRequests((auth) -> auth
		.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(allowUrls).permitAll()
                .requestMatchers(swaggerUrls).permitAll()
//            .requestMatchers("/api/v1/admin").hasRole("ADMIN") // ex. 관리자 권한 가진 사용자만 접근 가능
//            .requestMatchers("/api/v1/customer").hasRole("CUSTOMER") // ex. 고객 권한 가진 사용자만 접근 가능
//            .requestMatchers("/api/v1/manager").hasRole("MANAGER") // ex. 매니저 권한 가진 사용자만 접근 가능
                .anyRequest().authenticated() // 나머지 모든 요청 인증 필요
        );

    http
        // 로그인 필터 (JWT 발급)
        .addFilterAt(signinFilter, UsernamePasswordAuthenticationFilter.class)
        // 리프레시 토큰 필터 (갱신 요청만 처리)
        .addFilterBefore(refreshTokenFilter, UsernamePasswordAuthenticationFilter.class)
        // 일반 요청 AccessToken 처리 필터
        .addFilterBefore(new AccessTokenFilter(jwtTokenProvider, customUserDetailsService), UsernamePasswordAuthenticationFilter.class);


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

    // 🔑 배포환경 도메인 추가
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:3000", 
        "https://kbe-5-home-aid-fe.vercel.app", 
        "https://homeaid-service.com"
    ));

    // 🔑 허용할 HTTP 메서드
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

    // 🔑 허용할 헤더
    // TODO 배포 후 https로 바꾸면 보안 설정 추가해야 함
    // 프론트엔드 도메인 허용
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // React 개발 서버
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // 허용할 HTTP 메서드
    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization",
        "Content-Type",
        "X-Requested-With",
        "Accept",
        "Origin",
        "Access-Control-Request-Method",
        "Access-Control-Request-Headers"
    ));

    // 🔑 응답 헤더 중 노출할 것들
    configuration.setExposedHeaders(Arrays.asList("Authorization"));

    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }	
}
