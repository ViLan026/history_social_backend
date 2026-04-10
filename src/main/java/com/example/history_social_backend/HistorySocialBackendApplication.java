package com.example.history_social_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HistorySocialBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HistorySocialBackendApplication.class, args);
	}

}
