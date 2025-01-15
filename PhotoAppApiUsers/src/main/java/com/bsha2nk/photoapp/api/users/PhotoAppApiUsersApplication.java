package com.bsha2nk.photoapp.api.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import feign.Logger;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class PhotoAppApiUsersApplication {
	
	@Autowired
	Environment env;

	public static void main(String[] args) {
		SpringApplication.run(PhotoAppApiUsersApplication.class, args);
	}

	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	HttpExchangeRepository httpExchange() {
		return new InMemoryHttpExchangeRepository();
	}

	@Bean
	@LoadBalanced
	RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	Logger.Level getFeignLoggerLevel() {
		return Logger.Level.FULL;
	}

    @Bean
    @Profile("prod")
    String createProductionBean() {
		System.out.println("Production bean created. application.environment = " + env.getProperty("application.environment"));
		System.out.println("Production bean created. local.application.environment = " + env.getProperty("local.application.environment"));
		return "Production bean";
	}
	
	@Bean
	@Profile("!prod")
	String createNotProductionBean() {
		System.out.println("Not Production bean created. application.environment = " + env.getProperty("application.environment"));
		System.out.println("Not Production bean created. local.application.environment = " + env.getProperty("local.application.environment"));
		return "Not Production bean";
	}
	
	@Bean
	@Profile("default")
	String getProductionBean() {
		System.out.println("Default bean created. application.environment = " + env.getProperty("application.environment"));
		System.out.println("Default bean created. local.application.environment = " + env.getProperty("local.application.environment"));
		return "Default bean";
	}

}