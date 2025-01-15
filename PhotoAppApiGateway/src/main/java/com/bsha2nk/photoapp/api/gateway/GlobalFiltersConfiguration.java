package com.bsha2nk.photoapp.api.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import reactor.core.publisher.Mono;

@Configuration
public class GlobalFiltersConfiguration {

	final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean
	@Order(2)
	GlobalFilter secondFilters() {
		return (exchange, chain) -> {
			logger.info("2nd pre-filter executed");

			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				logger.info("2nd post-filter executed");
			}));
		};
	}
	
	@Bean
	@Order(3)
	GlobalFilter thirdFilters() {
		return (exchange, chain) -> {
			logger.info("3rd pre-filter executed");
			
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				logger.info("3rd post-filter executed");
			}));
		};
	}
	
	@Bean
	@Order(1)
	GlobalFilter fourthFilters() {
		return (exchange, chain) -> {
			logger.info("4th pre-filter executed");
			
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				logger.info("4th post-filter executed");
			}));
		};
	}
	
}