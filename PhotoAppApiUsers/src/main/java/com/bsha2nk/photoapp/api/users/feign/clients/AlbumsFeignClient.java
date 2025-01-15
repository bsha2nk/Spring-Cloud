package com.bsha2nk.photoapp.api.users.feign.clients;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.bsha2nk.photoapp.api.users.model.AlbumResponseModel;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "albums-ms")
public interface AlbumsFeignClient {

	@GetMapping("/users/{id}/albums")
	@Retry(name = "albums-ms")
	@CircuitBreaker(name = "albums-ms", fallbackMethod = "getAlbumsFallback")
	public List<AlbumResponseModel> getAlbums(@PathVariable String id, @RequestHeader("Authorization") String authorization);
	
	default List<AlbumResponseModel> getAlbumsFallback(String id, String authorization, Throwable exception) {
		return new ArrayList<>();
	}
	
}