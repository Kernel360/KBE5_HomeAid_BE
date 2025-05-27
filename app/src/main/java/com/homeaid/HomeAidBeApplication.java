package com.homeaid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.homeaid")
public class HomeAidBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeAidBeApplication.class, args);
	}

}
