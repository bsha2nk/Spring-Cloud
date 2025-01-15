package com.bsha2nk.photoapp.api.users.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.bsha2nk.photoapp.api.users.service.UsersService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurity {
	
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private UsersService userService;
	private Environment environment;
	
	public WebSecurity(BCryptPasswordEncoder bCryptPasswordEncoder, UsersService service, Environment environment) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userService = service;
		this.environment = environment;
	}

	@Bean
	protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {

		AuthenticationManagerBuilder builder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
		builder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
		AuthenticationManager authenticationManager = builder.build();
		
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, userService, environment);
		authenticationFilter.setFilterProcessesUrl("/users/login");

		httpSecurity.authorizeHttpRequests(authorise -> authorise
				.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
				.requestMatchers("/users/**").permitAll()
//				.requestMatchers(HttpMethod.POST, "/users").permitAll()
				.requestMatchers("/h2-console/**").permitAll()
				.anyRequest().permitAll())
		.addFilter(new AuthorizationFilter(authenticationManager, environment))
		.addFilter(authenticationFilter)
		.authenticationManager(authenticationManager)
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		httpSecurity.headers(heads -> heads.frameOptions(frame -> frame.disable()));

		httpSecurity.csrf(AbstractHttpConfigurer::disable);

		return httpSecurity.build();
	}
}
