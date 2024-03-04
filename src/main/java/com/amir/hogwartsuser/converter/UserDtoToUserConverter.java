package com.amir.hogwartsuser.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.amir.hogwartsuser.HogwartsUser;
import com.amir.hogwartsuser.dto.UserDto;

@Component
public class UserDtoToUserConverter implements Converter<UserDto, HogwartsUser>{

	@Override
	public HogwartsUser convert(UserDto source) {
		HogwartsUser hogwartsUser = new HogwartsUser();
		hogwartsUser.setId(source.id());
		hogwartsUser.setUsername(source.username());
		hogwartsUser.setEnabled(source.enabled());
		hogwartsUser.setRoles(source.roles());
		return hogwartsUser;
	}

}
