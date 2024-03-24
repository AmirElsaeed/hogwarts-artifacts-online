package com.amir.hogwartsuser;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amir.hogwartsuser.converter.UserDtoToUserConverter;
import com.amir.hogwartsuser.converter.UserToUserDtoConverter;
import com.amir.hogwartsuser.dto.UserDto;
import com.amir.system.Result;
import com.amir.system.StatusCode;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class UserController {
	
	private final UserService userService;
	
	private final UserToUserDtoConverter userToUserDtoConverter;
	
	private final UserDtoToUserConverter userDtoToUserConverter;

	public UserController(UserService userService, UserToUserDtoConverter userToUserDtoConverter,
			UserDtoToUserConverter userDtoToUserConverter) {
		this.userService = userService;
		this.userToUserDtoConverter = userToUserDtoConverter;
		this.userDtoToUserConverter = userDtoToUserConverter;
	}
	
	@GetMapping
	public Result findAllUsers() {
		List<HogwartsUser> hogwartsUsers = this.userService.findAll();
		List<UserDto> userDtos = hogwartsUsers.stream().map(this.userToUserDtoConverter::convert)
								 .collect(Collectors.toList());
		return new Result(true, StatusCode.SUCCESS, "Find All Success", userDtos);
	}
	
	@GetMapping("/{userId}")
	public Result findUserById(@PathVariable Integer userId) {
		HogwartsUser hogwartsUser = this.userService.findById(userId);
		UserDto userDto = this.userToUserDtoConverter.convert(hogwartsUser);
		return new Result(true, StatusCode.SUCCESS, "Find One Success", userDto);
	}
	
	@PostMapping
	public Result addUser(@Valid @RequestBody HogwartsUser newHogwartsUser) {
		HogwartsUser hogwartsUser = this.userService.save(newHogwartsUser);
		UserDto userDto = this.userToUserDtoConverter.convert(hogwartsUser);
		return new Result(true, StatusCode.SUCCESS, "Add Success", userDto);
	}
	
	@PutMapping("/{userId}")
	public Result updateUser(@PathVariable Integer userId, @Valid @RequestBody UserDto userDto) {
		HogwartsUser update = this.userDtoToUserConverter.convert(userDto);
		HogwartsUser updateHogwartsUser = this.userService.update(userId, update);
		UserDto updateUserDto = this.userToUserDtoConverter.convert(updateHogwartsUser);
		return new Result(true, StatusCode.SUCCESS, "Update Success", updateUserDto);
	}
	
	@DeleteMapping("/{userId}")
	public Result deleteUser(@PathVariable Integer userId) {
		this.userService.delete(userId);
		return new Result(true, StatusCode.SUCCESS, "Delete Success");
	}
}
