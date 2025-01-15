package com.bsha2nk.photoapp.api.users.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

	private String email;
	
	private String password;

}