package com.amir.hogwartsuser;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.amir.system.exception.ObjectNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService implements UserDetailsService{
	
	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	public List<HogwartsUser> findAll() {
		return this.userRepository.findAll();
	}
	
	public HogwartsUser findById(Integer userId) {
		return this.userRepository.findById(userId)
				.orElseThrow(() -> new ObjectNotFoundException("user", userId));
	}
	
	public HogwartsUser save(HogwartsUser newHogwartsUser) {
		newHogwartsUser.setPassword(this.passwordEncoder.encode(newHogwartsUser.getPassword()));
		return this.userRepository.save(newHogwartsUser);
	}
	
	public HogwartsUser update(Integer userId, HogwartsUser update) {
		return this.userRepository.findById(userId)
				.map(oldHogwartsUser -> {
					oldHogwartsUser.setUsername(update.getUsername());
					oldHogwartsUser.setEnabled(update.isEnabled());
					oldHogwartsUser.setRoles(update.getRoles());
					return this.userRepository.save(oldHogwartsUser);
				}).orElseThrow(() -> new ObjectNotFoundException("user", userId));
	}
	
	public void delete(Integer userId) {
		this.userRepository.findById(userId)
		.orElseThrow(() -> new ObjectNotFoundException("user", userId));
		this.userRepository.deleteById(userId);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.err.println("loadUserByUsername");
		// findByUsername return HogwartsUser but i need UserDetails so i am using adapter pattern here
		return this.userRepository.findByUsername(username)
		.map(hogwartsUser -> new MyUserPrincipal(hogwartsUser))
		.orElseThrow(() -> new UsernameNotFoundException("username " + username +" is not found."));
	}
}
