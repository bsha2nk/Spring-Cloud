package com.bsha2nk.photoapp.api.users.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bsha2nk.photoapp.api.users.DTO.UserDTO;
import com.bsha2nk.photoapp.api.users.entity.AuthorityEntity;
import com.bsha2nk.photoapp.api.users.entity.RoleEntity;
import com.bsha2nk.photoapp.api.users.entity.UserEntity;
import com.bsha2nk.photoapp.api.users.feign.clients.AlbumsFeignClient;
import com.bsha2nk.photoapp.api.users.model.AlbumResponseModel;
import com.bsha2nk.photoapp.api.users.repository.UserRepository;
import com.bsha2nk.photoapp.api.users.service.UsersService;

@Service
public class UsersServiceImpl implements UsersService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AlbumsFeignClient albumsFeignClient;

	@Autowired
	private Environment environment;

	private Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);
	
	@Override
	public UserDTO createUser(UserDTO userDTO) {
		userDTO.setUserId(UUID.randomUUID().toString());
		userDTO.setEncryptedPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		UserEntity userEntity = mapper.map(userDTO, UserEntity.class);

		UserEntity createdUser = userRepository.save(userEntity);

		return mapper.map(createdUser, UserDTO.class);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(username);

		if (userEntity == null)
			throw new UsernameNotFoundException(username);

		Collection<GrantedAuthority> authorities = new ArrayList<>();
		Collection<RoleEntity> userRoles = userEntity.getRoles();

		userRoles.forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
			Collection<AuthorityEntity> userAuthorities = role.getAuthorities();
			userAuthorities.forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority.getName())));
		});

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, authorities);
	}

	@Override
	public UserDTO getUserDetailsByEmail(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		return new ModelMapper().map(userEntity, UserDTO.class);
	}

	@Override
	public UserDTO getUserByUserId(String userId, String authorization) {
		UserEntity userEntity = userRepository.findByUserId(userId);

		if (userEntity == null)
			throw new UsernameNotFoundException("User not found!");

		UserDTO userDTO = new ModelMapper().map(userEntity, UserDTO.class);

//		String albumsURL = String.format(environment.getProperty("albums.url"), userId);
//		ResponseEntity<List<AlbumResponseModel>> albumsResponseList = restTemplate.exchange(albumsURL, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {});

		logger.debug("Before calling albums microservice");
		List<AlbumResponseModel> albumsResponseList = albumsFeignClient.getAlbums(userId, authorization);
		logger.debug("After calling albums microservice");

		userDTO.setAlbums(albumsResponseList);

		return userDTO;
	}

}