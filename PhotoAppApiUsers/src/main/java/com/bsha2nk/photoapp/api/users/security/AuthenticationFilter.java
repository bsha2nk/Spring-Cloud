package com.bsha2nk.photoapp.api.users.security;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bsha2nk.photoapp.api.users.DTO.LoginRequestDTO;
import com.bsha2nk.photoapp.api.users.DTO.UserDTO;
import com.bsha2nk.photoapp.api.users.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private UsersService userService;
	private Environment environment;
	
	public AuthenticationFilter(AuthenticationManager authenticationManager, UsersService service, Environment environment) {
		super(authenticationManager);
		this.userService = service;
		this.environment = environment;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		try {
			LoginRequestDTO creds = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDTO.class);

			return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		String userName = ((User)authResult.getPrincipal()).getUsername();
		UserDTO userDTO = userService.getUserDetailsByEmail(userName);
		
		String tokenSecret = environment.getProperty("token.secret");
		long tokenExpiration = Long.parseLong(environment.getProperty("token.expiration-time"));
		byte[] secretBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
		SecretKey secretKey = Keys.hmacShaKeyFor(secretBytes);
		
		Instant now = Instant.now();
		String token = Jwts.builder()
		.subject(userDTO.getUserId())
		.claim("scope", authResult.getAuthorities())
		.expiration(Date.from(now.plusMillis(tokenExpiration)))
		.issuedAt(Date.from(now))
		.signWith(secretKey)
		.compact();
		
		response.addHeader("token", token);
		response.addHeader("userId", userDTO.getUserId());
	}

}
