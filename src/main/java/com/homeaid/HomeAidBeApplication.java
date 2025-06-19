package com.homeaid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.homeaid")
@EnableJpaAuditing
@EnableAsync
public class HomeAidBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeAidBeApplication.class, args);
	}

}
