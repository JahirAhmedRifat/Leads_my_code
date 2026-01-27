package com.example.jwtExample.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.jwtExample.models.User;
import com.example.jwtExample.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

//	@Autowired
//	private BCryptPasswordEncoder bpasswordEncoder;

//	----------- static data added ------------
//	private List<User> store=new ArrayList<>();
//	public UserService() {
//		store.add(new User(UUID.randomUUID().toString(), "Md. Jahir", "abc@gmail.com"));
//		store.add(new User(UUID.randomUUID().toString(), "Rifat", "123@gmail.com"));
//		store.add(new User(UUID.randomUUID().toString(), "Md.khan", "023abc@gmail.com"));
//		store.add(new User(UUID.randomUUID().toString(), "Alom", "abc001@gmail.com"));
//	}
//	public List<User> getUsers(){
//		return this.store;
//	}  

	public List<User> getUsers() {
		return userRepository.findAll();
	}
	
//	---------------- get user by userId ----------
	public Optional<User> getUserByUserId(String userId) {
		return userRepository.findById(userId);
	}

	public User createUser(User user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new IllegalArgumentException("Email already exists!");
		}
		// user.setUserId(UUID.randomUUID().toString());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

//	----------------------- get user by email & encode password ------------------------
	public Optional<User> getUserByEmailAndPassword(String email, String password) {
		Optional<User> user = userRepository.findByEmail(email);
		if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {

			return user; // right data

		}

		return Optional.empty(); // wrong data
	}

}
