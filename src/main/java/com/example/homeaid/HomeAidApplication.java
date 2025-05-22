package com.example.homeaid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HomeAidApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeAidApplication.class, args);
    }

}
