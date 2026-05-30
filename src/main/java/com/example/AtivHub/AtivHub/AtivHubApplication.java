package com.example.AtivHub.AtivHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

// Recriando o arquivo para forçar a reindexação pela IDE
@SpringBootApplication
@EnableMethodSecurity
public class AtivHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtivHubApplication.class, args);
	}

}