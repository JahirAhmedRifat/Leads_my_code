package com.example.jwtExample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class AppConfig {
	
// user & password set kora (atai email & password JWT er)// chaila "application.properties" ya o set kora jai- 
	
//	@Bean
//	public UserDetailsService userDetailsService() {
//		UserDetails userDetails = User.builder().username("jahir").password(passwordEncoder().encode("123"))
//				.roles("ADMIN").build();
//		UserDetails userDetails02 = User.builder().username("rifat").password(passwordEncoder().encode("321"))
//				.roles("ADMIN").build();
//		return new InMemoryUserDetailsManager(userDetails, userDetails02);
//	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
//	---------------------------------------

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
		return builder.getAuthenticationManager();
	}

}
