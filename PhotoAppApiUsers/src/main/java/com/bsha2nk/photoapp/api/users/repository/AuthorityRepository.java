package com.bsha2nk.photoapp.api.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bsha2nk.photoapp.api.users.entity.AuthorityEntity;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {

	 AuthorityEntity findByName(String authName);
	 
}