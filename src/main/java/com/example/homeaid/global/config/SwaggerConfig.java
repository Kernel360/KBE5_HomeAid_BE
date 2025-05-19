package com.example.homeaid.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI springOpenAPI() {

    return new OpenAPI()
        .info(new Info()
            .title("HomeAid API 명세서")
            .version("1.0")
            .description("HomeAid는 매칭시스템을 활용하여 고객에게 맞춤형 매니저를 매칭시켜주고 가사 및 청소 서비스를 제공합니다."));

  }
}
