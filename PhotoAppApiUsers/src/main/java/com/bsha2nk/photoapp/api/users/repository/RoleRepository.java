package com.bsha2nk.photoapp.api.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bsha2nk.photoapp.api.users.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

	RoleEntity findByName(String roleName);
}
