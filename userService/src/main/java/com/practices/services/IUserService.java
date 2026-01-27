package com.practices.services;

import com.practices.dto.UserRequest;
import com.practices.dto.response.UserResponse;

import java.util.List;

public interface IUserService {
//    UserRequest saveUser(UserRequest user);
//    UserRequest updateUser(UserRequest user);
//    UserRequest deleteUserByUserId(String userId);
//    List<UserRequest> getAllUser();
//    UserRequest getUserByUserId(String userId);

    UserResponse saveUser(UserRequest user);
    UserResponse updateUser(UserRequest user);
    UserResponse deleteUserByUserId(String userId);
    List<UserResponse> getAllUser();
    UserResponse getUserByUserId(String userId);
}
