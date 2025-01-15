package com.bsha2nk.photoapp.api.users.errorhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import feign.Response;
import feign.codec.ErrorDecoder;

@Component
public class FeignErrorDecoder implements ErrorDecoder {
	
	@Autowired
	private Environment env;
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Exception decode(String methodKey, Response response) {
		switch (response.status()) {
			case 400: 
				// throw related error
				break;
			case 404:
				if (methodKey.contains("getAlbums")) {
					logger.error(env.getProperty("albums.exception.albums-url-wrong") + ": " + response.request().url());
					return new ResponseStatusException(HttpStatus.valueOf(response.status()), env.getProperty("albums.exception.albums-url-wrong") + ": " + response.request().url());
				}
				break;
			default: return new Exception(response.reason());
		}
		
		return null;
	}

}