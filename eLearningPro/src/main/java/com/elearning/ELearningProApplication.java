package com.elearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.elearning.entity")
@EnableJpaRepositories("com.elearning.repository")
public class ELearningProApplication {

	public static void main(String[] args) {
		SpringApplication.run(ELearningProApplication.class, args);
	}

}
