package com.bsha2nk.photoapp.api.users.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.bsha2nk.photoapp.api.users.DTO.UserDTO;

public interface UsersService extends UserDetailsService {

    UserDTO createUser(UserDTO userDTO);
    
    UserDTO getUserDetailsByEmail(String email);
    
    UserDTO getUserByUserId(String userId, String authorization);
}
