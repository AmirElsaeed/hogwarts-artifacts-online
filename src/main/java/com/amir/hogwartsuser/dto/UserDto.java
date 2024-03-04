package com.amir.hogwartsuser.dto;

import jakarta.validation.constraints.NotEmpty;

public record UserDto(Integer id,
		
					  @NotEmpty(message = "username is required")
					  String username,
					  
					  //String password; // we will not use password here
					  
					  boolean enabled,
					  
					  @NotEmpty(message = "roles are required")
					  String roles) {

}
