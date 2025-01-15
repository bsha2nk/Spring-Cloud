package com.bsha2nk.photoapp.api.users.repository;

import com.bsha2nk.photoapp.api.users.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

	UserEntity findByEmail(String email);
	
	UserEntity findByUserId(String userId);
}
