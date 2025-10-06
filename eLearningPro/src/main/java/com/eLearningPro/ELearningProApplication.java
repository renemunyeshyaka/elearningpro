package com.eLearningPro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.eLearningPro.entity")
@EnableJpaRepositories("com.eLearningPro.repository")
public class ELearningProApplication {

	public static void main(String[] args) {
		SpringApplication.run(ELearningProApplication.class, args);
	}

}
