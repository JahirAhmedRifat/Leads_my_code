package com.example.jwtExample.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.jwtExample.models.User;
import com.example.jwtExample.service.UserService;

@RestController
@RequestMapping("/home")
public class HomeController {

	@Autowired
	private UserService userService;

	// http://localhost:8081/home/users

	// --------- get all data ------
	@GetMapping("/users")
	public List<User> getUser() {
		System.out.println("getting users");
		return userService.getUsers();
	}

	// --------- find by userId------
	@GetMapping("/getData/{userId}")
	public Optional<User> getUserByUserId(@PathVariable("userId") String userId) {
		System.out.println("getting users .....");
		return userService.getUserByUserId(userId);
	}

	// -------- get user by email & encode password ---------
	@PostMapping("/getuser")
	public ResponseEntity<?> getUserByEmailAndPassword(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		String password = request.get("password");

		Optional<User> user = userService.getUserByEmailAndPassword(email, password);

		if (user.isPresent()) {
			return ResponseEntity.ok(user.get()); // if success then get data
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
		}
	}

	
//	@GetMapping("/getuser")
//	public User getSpecificUser(@RequestParam String email, @RequestParam String password) {
//		return userService.getUserByEmailAndPassword(email, password);
//	}

	// checked which user logged in ---
	@GetMapping("/current_user")
	public String getLoggedInUser(Principal principal) {
		return principal.getName();
	}

}
