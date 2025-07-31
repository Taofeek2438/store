package com.codewithtaofeek.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.codewithtaofeek.store.config",
		"com.codewithtaofeek.store.service",
		"com.codewithtaofeek.store.controller",
		"com.codewithtaofeek.store.repository",
		"com.codewithtaofeek.store.security"
})
@EntityScan(basePackages = "com.codewithtaofeek.store.entity")
@EnableJpaRepositories(basePackages = "com.codewithtaofeek.store.repository")
public class StoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreApplication.class, args);
	}

}
