package com.security.spring_boot_security.configuration;

import com.security.spring_boot_security.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true) // for single authorization
public class MySecurityConfig {

    //---- basic authentication ----------------
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/public/**").permitAll()
//                      //.requestMatchers("/public/**").hasRole("NORMAL")
//                        .requestMatchers("/users/**").hasRole("ADMIN")
//                        .anyRequest()
//                        .authenticated()
//                )
//                .httpBasic(httpBasic -> {});
//        return http.build();
//
//    }

    //---- form based authentication ----------------
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()
                        //.requestMatchers("/public/**").hasRole("NORMAL")
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(form -> form
                        //.loginPage("/login")  // custom login page (optional)
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());  // optional logout config

        return http.build();

    }

    // AuthenticationManager bean -->> which is use for user setting---
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.inMemoryAuthentication()
                .withUser("john").password(passwordEncoder().encode("123")).roles("NORMAL")
                .and()
                .withUser("rose").password(passwordEncoder().encode("1234")).roles("ADMIN");
        return builder.build();
    }

// ---------same as authentucationManager -------------------
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails admin = User.withUsername("admin")
//                .password(passwordEncoder().encode("admin123"))
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withUsername("user")
//                .password(passwordEncoder().encode("user123"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }


    // Password encoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
//        return NoOpPasswordEncoder.getInstance();
    }








    //---------------------- for database authentication & Authorization -----------

//    private final CustomUserDetailsService customUserDetailsService;
//
//    public MySecurityConfig(CustomUserDetailsService customUserDetailsService) {
//        this.customUserDetailsService = customUserDetailsService;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//            // ------------ database authentication -----------
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http,
//                                                       PasswordEncoder passwordEncoder,
//                                                       CustomUserDetailsService customUserDetailsService
//                                                       ) throws Exception {
//
//        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        builder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
//        return builder.build();
//    }
//
//    //---- form based authentication ----------------
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/public/**").permitAll()
//                        //.requestMatchers("/public/**").hasRole("NORMAL")
//                        .requestMatchers("/users/**").hasRole("ADMIN")
//                        .anyRequest()
//                        .authenticated()
//                )
//                .formLogin(form -> form
//                        //.loginPage("/login")  // custom login page (optional)
//                        .permitAll()
//                )
//                .logout(logout -> logout.permitAll());  // optional logout config
//
//        return http.build();
//
//    }






}






// ----------------------------------

//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/public/**").permitAll() // public endpoint free
//                        .requestMatchers("/admin/**").hasRole("ADMIN") // only admin
//                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // user/admin
//                        .anyRequest().authenticated() // বাকিগুলো লগইন ছাড়া অ্যাক্সেস হবে না
//                )
//                .formLogin(Customizer.withDefaults()) // default login page
//                .logout(Customizer.withDefaults()); // default logout
//        return http.build();
//    }
//
                // InMemory authentication
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails admin = User.withUsername("admin")
//                .password(passwordEncoder().encode("admin123"))
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withUsername("user")
//                .password(passwordEncoder().encode("user123"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}

