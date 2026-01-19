package com.example.backjeon_BE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BackjeonBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackjeonBeApplication.class, args);
	}

}
