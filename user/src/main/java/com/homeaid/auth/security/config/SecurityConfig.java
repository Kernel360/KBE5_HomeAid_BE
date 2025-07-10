package com.homeaid.auth.security.config;

import com.homeaid.auth.security.filter.OAuth2AuthenticationSuccessHandler;
import com.homeaid.auth.service.RefreshTokenService;
import com.homeaid.auth.service.TokenBlacklistService;
import com.homeaid.auth.security.filter.AccessTokenFilter;
import com.homeaid.auth.security.filter.JwtAuthenticationFilter;
import com.homeaid.auth.security.jwt.JwtTokenProvider;
import com.homeaid.auth.service.CustomOAuth2UserService;
import com.homeaid.auth.util.CookieUtil;
import java.util.Arrays;
import java.util.Collections;
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

  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;
  private final TokenBlacklistService tokenBlacklistService;
  private final CookieUtil cookieUtil;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;

  // TODO 권한별 requestMatchers uri 정리 필요
  private final String[] allowUrls = {
      "/", "/actuator/health",
      "/api/v1/auth/signup/**",
      "/api/v1/swagger/auth/**",
      "/api/v1/auth/**",
      "api/v1/oauth2/**",
      "/api/v1/users/my/**",
      "api/v1/reservations/**",
      "api/v1/managers/**",
      "api/v1/reviews/**",
      "api/v1/**"
  };

  private final String[] swaggerUrls = {
      "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
      "/configuration/ui", "/configuration/security", "/webjars/**"
  };

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager)
      throws Exception {
    JwtAuthenticationFilter signinFilter = new JwtAuthenticationFilter(authManager,
        jwtTokenProvider, refreshTokenService, cookieUtil);
    signinFilter.setFilterProcessesUrl("/api/v1/auth/signin");

    http
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(allowUrls).permitAll()
                .requestMatchers(swaggerUrls).permitAll()
//        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
//        .requestMatchers("/api/v1/customer/**").hasRole("CUSTOMER")
            .requestMatchers("/api/v1/manager/**").hasRole("MANAGER")
            .requestMatchers("/api/v1/reservations/*/issues").hasAnyRole("MANAGER", "CUSTOMER")
            .requestMatchers("/api/v1/alerts/connection").hasAnyRole("CUSTOMER", "MANAGER", "ADMIN")
//        .requestMatchers("/api/v1/**").hasAnyRole("ADMIN", "USER", "MANAGER")
                .anyRequest().authenticated()
        )
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService))
            .successHandler(oAuth2SuccessHandler)
        );

    http
        .addFilterAt(signinFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new AccessTokenFilter(jwtTokenProvider, tokenBlacklistService),
            UsernamePasswordAuthenticationFilter.class);

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

  // ✅ CORS 설정 중복 제거
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:3000",
        "https://kbe-5-home-aid-fe.vercel.app",
        "https://homeaid-service.com"
    ));

    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization",
        "Content-Type",
        "X-Requested-With",
        "Accept",
        "Origin",
        "Access-Control-Request-Method",
        "Access-Control-Request-Headers"
    ));

    configuration.setExposedHeaders(Collections.singletonList("Authorization"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
