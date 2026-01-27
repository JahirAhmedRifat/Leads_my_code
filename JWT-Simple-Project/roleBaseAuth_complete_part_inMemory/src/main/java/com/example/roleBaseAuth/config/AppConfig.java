package com.example.roleBaseAuth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;

@Configuration
public class AppConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails admin = User.builder().username("admin").password(passwordEncoder().encode("1234")).roles("ADMIN")
				.build();
		UserDetails user = User.builder().username("user").password(passwordEncoder().encode("1234")).roles("USER")
				.build();
		return new InMemoryUserDetailsManager(admin, user);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/admin/**").hasRole("ADMIN")
						.requestMatchers("/auth/user/**").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/auth/public/**", "/error").permitAll()
						.anyRequest().authenticated())
				.httpBasic(Customizer.withDefaults())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
	
	// .requestMatchers("/auth/admin/**").hasRole("ADMIN")
	// .requestMatchers(HttpMethod.GET).hasRole("ADMIN")
	// .requestMatchers(HttpMethod.POST).hasRole("ADMIN")
	// .requestMatchers(HttpMethod.PUT).hasRole("USER")
	// .requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")
	
	

//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		http.csrf(csrf -> csrf.disable())
//				.authorizeHttpRequests(auth -> auth.requestMatchers("/admin/**").hasRole("ADMIN")
//						.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
//						.requestMatchers("/public/**", "/error").permitAll().anyRequest().authenticated())
//				.httpBasic(Customizer.withDefaults())
//				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//		return http.build();
//	}

}
