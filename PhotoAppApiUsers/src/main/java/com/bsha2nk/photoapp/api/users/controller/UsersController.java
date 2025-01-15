package com.bsha2nk.photoapp.api.users.controller;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bsha2nk.photoapp.api.users.DTO.CreateUserDTO;
import com.bsha2nk.photoapp.api.users.DTO.CreateUserResponseDTO;
import com.bsha2nk.photoapp.api.users.DTO.UserDTO;
import com.bsha2nk.photoapp.api.users.model.UserResponseModel;
import com.bsha2nk.photoapp.api.users.service.UsersService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {

	@Autowired
	private UsersService usersService;
	
	@Autowired
	Environment env;

	@GetMapping("/status")
	public String status() {
		return "Users Microservce is Working on port " + env.getProperty("local.server.port") + ", with token expiry time " + env.getProperty("token.expiration-time");
	}

	@PostMapping
	public ResponseEntity<CreateUserResponseDTO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		UserDTO userDTO = mapper.map(createUserDTO, UserDTO.class);
		UserDTO createdUser = usersService.createUser(userDTO);

		CreateUserResponseDTO responseDTO = mapper.map(createdUser, CreateUserResponseDTO.class);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
//		return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
	}
	
	@GetMapping("/{userId}")
	@PreAuthorize("hasRole('ADMIN') or principal == #userId")
//	@PreAuthorize("principal == #userId")
//	@PostAuthorize("principal == returnObject.body.userId")
	public ResponseEntity<UserResponseModel> getUser(@PathVariable String userId, @RequestHeader("Authorization") String authorization) {
		UserDTO userDTO = usersService.getUserByUserId(userId, authorization);
		
		UserResponseModel returnValue = new ModelMapper().map(userDTO, UserResponseModel.class);
		
		return ResponseEntity.ok(returnValue);
	}

	@DeleteMapping("{userId}")
	@PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE') or principal == #userId")
	public String deleteUser(@PathVariable String userId) {
		return "Deleted user with id " + userId;
	}
	
}