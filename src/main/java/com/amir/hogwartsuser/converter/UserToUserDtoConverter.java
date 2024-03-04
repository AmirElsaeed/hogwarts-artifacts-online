package com.amir.hogwartsuser.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.amir.hogwartsuser.HogwartsUser;
import com.amir.hogwartsuser.dto.UserDto;

@Component
public class UserToUserDtoConverter implements Converter<HogwartsUser, UserDto> {

	@Override
	public UserDto convert(HogwartsUser source) {
		UserDto userDto = new UserDto(source.getId(), source.getUsername(), source.isEnabled(), source.getRoles());
		return userDto;
	}

}
