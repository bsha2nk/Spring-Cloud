package com.bsha2nk.photoapp.api.gateway;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.lang.Arrays;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.core.HttpHeaders;
import reactor.core.publisher.Mono;

@Component
public class AuthorisationHeaderFilter extends AbstractGatewayFilterFactory<AuthorisationHeaderFilter.Config> {

	@Autowired
	Environment environment;

	public AuthorisationHeaderFilter() {
		super(Config.class);
	}

	public static class Config {
		private List<String> authorities;
//		private String role;
//		private String authority;

		public List<String> getAuthorities() {
			return authorities;
		}

		public void setAuthorities(String authorities) {
			this.authorities = Arrays.asList(authorities.split(" "));
		}
		
//		private String getRole() {
//			return role;
//		}
//
//		public void setRole(String role) {
//			this.role = role;
//		}
//
//		private String getAuthority() {
//			return authority;
//		}
//
//		public void setAuthority(String authority) {
//			this.authority = authority;
//		}
	}
	
	@Override
	public List<String> shortcutFieldOrder() {
		return List.of("authorities");
	}
	
//	@Override
//	public List<String> shortcutFieldOrder() {
//		return List.of("role", "authority");
//	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();

			if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "No Authorisation header!", HttpStatus.UNAUTHORIZED);
			}

			String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwt = authHeader.replace("Bearer ", "");

			List<String> authorities = getAuthorities(jwt);
			
			boolean hasRequiredAuthority = authorities.stream()
					.anyMatch(authority -> config.getAuthorities().contains(authority));
			
			if (!hasRequiredAuthority)
				return onError(exchange, "User is not authorised to perform this action.", HttpStatus.FORBIDDEN);
			
//			if (!isJwtValid(jwt)) {
//				return onError(exchange, "No Authorisation header!", HttpStatus.UNAUTHORIZED);
//			}

			return chain.filter(exchange);
		};
	}

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus unauthorized) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(unauthorized);
		
		DataBufferFactory factory = response.bufferFactory();
		DataBuffer buffer = factory.wrap(err.getBytes());
		
		return response.writeWith(Mono.just(buffer));
	}

	private List<String> getAuthorities(String jwt) {
		List<String> authorities = new ArrayList<>();

		String secret = environment.getProperty("token.secret");
		byte[] secretKeyBytes = Base64.getEncoder().encode(secret.getBytes());
		SecretKey signingKey = Keys.hmacShaKeyFor(secretKeyBytes);

		JwtParser jwtParser = Jwts.parser().verifyWith(signingKey).build();

		try {
			List<Map<String, String>> scopes = ((Claims) jwtParser.parse(jwt).getPayload()).get("scope", List.class);
			scopes.stream().forEach(scopeMap -> authorities.add(scopeMap.get("authority")));
		} catch (Exception e) {
			return authorities;
		}

		return authorities;
	}

	private boolean isJwtValid(String jwt) {
		boolean isValid = true;

		String subject = null;
		String secret = environment.getProperty("token.secret");
		byte[] secretKeyBytes = Base64.getEncoder().encode(secret.getBytes());
		SecretKey signingKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());

		JwtParser jwtParser = Jwts.parser().setSigningKey(secretKeyBytes).build();

		try {
			Jws<Claims> parsedToken = jwtParser.parseClaimsJws(jwt);
			subject = parsedToken.getBody().getSubject();
		} catch (Exception e) {
			isValid = false;
		}

		if (subject == null || subject.isEmpty()) {
			isValid = false;
		}

		return isValid;
	}

}
