package com.security.spring_boot_security.controllers;

import com.security.spring_boot_security.common.ApiResponse;
import com.security.spring_boot_security.models.User;
import com.security.spring_boot_security.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public List<User> getUsers(){
        return this.userService.getAllUser();
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<User>> getUserByUsername(@PathVariable String username){
        User user= userService.getUser(username);
        ApiResponse<User> response= new ApiResponse<>(
                "User found successfully",
                true,
                HttpStatus.OK,
                user
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<User>> addUser(@RequestBody User user){
        User user1= userService.addUser(user);
        ApiResponse<User> response= new ApiResponse<>(
                "User create successfully",
                true,
                HttpStatus.CREATED,
                user1
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
