package com.bsha2nk.photoapp.discovery.PhotoAppDiscoveryService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurity {

	@Bean
	SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf(csrfCOnfig -> csrfCOnfig.disable())
                .authorizeHttpRequests(authorizeHttpRequestsCustomizer ->
                        authorizeHttpRequestsCustomizer.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
		
		return httpSecurity.build();
	}
}
