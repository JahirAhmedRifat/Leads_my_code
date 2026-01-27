package com.example.jwtToken.service;

import com.example.jwtToken.dto.UserRequest;
import com.example.jwtToken.dto.UserResponse;

import java.util.List;

public interface IUserService {
    UserResponse saveUser(UserRequest request);
    List<UserResponse> getAllUsers();
    UserResponse findByEmail(String email);
}
