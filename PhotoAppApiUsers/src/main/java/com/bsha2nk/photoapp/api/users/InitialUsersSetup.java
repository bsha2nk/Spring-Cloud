package com.bsha2nk.photoapp.api.users;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.bsha2nk.photoapp.api.users.entity.AuthorityEntity;
import com.bsha2nk.photoapp.api.users.entity.RoleEntity;
import com.bsha2nk.photoapp.api.users.entity.UserEntity;
import com.bsha2nk.photoapp.api.users.repository.AuthorityRepository;
import com.bsha2nk.photoapp.api.users.repository.RoleRepository;
import com.bsha2nk.photoapp.api.users.repository.UserRepository;

import jakarta.transaction.Transactional;

@Component
public class InitialUsersSetup {

	@Autowired
	AuthorityRepository authorityRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EventListener
	@Transactional
	public void onAppReady(ApplicationReadyEvent readyEvent) {
		logger.info("Application ready event triggered.");

		AuthorityEntity readAuthority = createAuthority("READ");
		AuthorityEntity writeAuthority = createAuthority("WRITE");
		AuthorityEntity deleteAuthority = createAuthority("DELETE");

		createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority));
		RoleEntity roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(readAuthority, writeAuthority, deleteAuthority));

		if (roleAdmin == null)
			return;

		UserEntity adminUser = new UserEntity();
		adminUser.setFirstName("Shashank");
		adminUser.setLastName("Balasa");
		adminUser.setEmail("admin@mail.com");
		adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("12345678"));
		adminUser.setUserId(UUID.randomUUID().toString());
		adminUser.setRoles(Arrays.asList(roleAdmin));

		UserEntity storedAdminUser = userRepository.findByEmail(adminUser.getEmail());
		if (storedAdminUser == null) {
			userRepository.save(adminUser);
		}

	}

	@Transactional
	public AuthorityEntity createAuthority(String name) {
		AuthorityEntity authority = authorityRepository.findByName(name);

		if (authority == null) {
			authority = new AuthorityEntity(name);
			authorityRepository.save(authority);
		}

		return authority;
	}

	@Transactional
	public RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
		RoleEntity roleEntity = roleRepository.findByName(name);

		if (roleEntity == null) {
			roleEntity = new RoleEntity(name, authorities);
			roleRepository.save(roleEntity);
		}

		return roleEntity;
	}

}