package com.practices;

import com.practices.entities.User;
import com.practices.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication implements CommandLineRunner {

//	@Autowired
//	private final UserRepository userRepository;

	private final UserRepository userRepository;
	public UserServiceApplication(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//---- 01 user create -----------
		if(userRepository.findByRole("ROLE_ADMIN").isEmpty()){
			User user1=new User()
					.setUserId("U01")
					.setUserName("Jahir")
					.setEmail("abc@gmail.com")
					.setPassword("1234")
					.setRole("ROLE_ADMIN");
			this.userRepository.save(user1);
			System.out.println("Default user created : " +user1.getUserName());
		}else {
			System.out.println("Default user already exists !! ");
		}
		//---- 02 user create -----------
		if(userRepository.findByRole("ROLE_USER").isEmpty()){
			User user1=new User()
					.setUserId("U02")
					.setUserName("Rifat")
					.setEmail("abc@gmail.com")
					.setPassword("12345")
					.setRole("ROLE_USER");
			this.userRepository.save(user1);
			System.out.println("Default user created : " +user1.getUserName());
		}else {
			System.out.println("Default user already exists !! ");
		}
	}
}
