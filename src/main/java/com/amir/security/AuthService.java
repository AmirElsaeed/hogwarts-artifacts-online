package com.amir.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.amir.hogwartsuser.HogwartsUser;
import com.amir.hogwartsuser.MyUserPrincipal;
import com.amir.hogwartsuser.converter.UserToUserDtoConverter;
import com.amir.hogwartsuser.dto.UserDto;

@Service
public class AuthService {
	
	private final JwtProvider jwtProvider;
	
	private final UserToUserDtoConverter userToUserDtoConverter;
	
	public AuthService(JwtProvider jwtProvider, UserToUserDtoConverter userToUserDtoConverter) {
		this.jwtProvider = jwtProvider;
		this.userToUserDtoConverter = userToUserDtoConverter;
	}

	public Map<String, Object> createLoginInfo(Authentication authentication) {
		// Create user info.
		MyUserPrincipal principal = (MyUserPrincipal)authentication.getPrincipal();
		HogwartsUser hogwartsUser = principal.getHogwartsUser();
		UserDto userDto = this.userToUserDtoConverter.convert(hogwartsUser);
		
		// Create a JWT
		String token = this.jwtProvider.createToken(authentication);
		
		Map<String, Object> loginResultMap = new HashMap<>();
		loginResultMap.put("userInfo", userDto);
		loginResultMap.put("token", token);
		
		return loginResultMap;
	}
	
}
