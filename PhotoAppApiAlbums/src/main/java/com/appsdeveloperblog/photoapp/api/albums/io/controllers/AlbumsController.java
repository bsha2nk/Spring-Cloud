package com.appsdeveloperblog.photoapp.api.albums.io.controllers;

import com.appsdeveloperblog.photoapp.api.albums.data.AlbumEntity;
import com.appsdeveloperblog.photoapp.api.albums.service.AlbumsService;
import com.appsdeveloperblog.photoapp.api.albums.ui.model.AlbumResponseModel;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import java.lang.reflect.Type;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/users/{id}/albums")
public class AlbumsController {

	@Autowired
	AlbumsService albumsService;
	
	@Autowired
	Environment env;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@PreAuthorize("hasRole('ADMIN') or principal == #id")
	public List<AlbumResponseModel> userAlbums(@PathVariable String id) {
		logger.info("Request routed to port: " + env.getProperty("local.server.port"));
		
		List<AlbumResponseModel> returnValue = new ArrayList<>();

		List<AlbumEntity> albumsEntities = albumsService.getAlbums(id);

		if(albumsEntities == null || albumsEntities.isEmpty()) {
			return returnValue;
		}

		Type listType = new TypeToken<List<AlbumResponseModel>>(){}.getType();

		returnValue = new ModelMapper().map(albumsEntities, listType);
		logger.info("Returning " + returnValue.size() + " albums");
		return returnValue;
	}
}
