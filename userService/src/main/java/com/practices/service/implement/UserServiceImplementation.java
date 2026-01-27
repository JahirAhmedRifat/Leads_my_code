package com.practices.service.implement;

import com.practices.common.RequestCleaner;
import com.practices.dto.UserRequest;
import com.practices.dto.response.UserResponse;
import com.practices.entities.User;
import com.practices.exception.DuplicateResourceException;
import com.practices.exception.ResourceNotFoundException;
import com.practices.repositories.UserRepository;
import com.practices.services.IUserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public UserResponse saveUser(UserRequest userRequest) {

        // Null or blank check
        String rawUserId = userRequest.getUserId();
        if (rawUserId == null || rawUserId.isBlank()) {
            throw new IllegalArgumentException("User ID must not be null or blank");
        }

        // Check for duplicate
        String userIdToCheck = rawUserId.trim().toLowerCase();
        if (userRepository.existsByUserIdIgnoreCaseAndIsDeletedFalse(userIdToCheck)) {
            throw new DuplicateResourceException("User ID already exists: " + rawUserId);
        }
        // Trim all fields safely
        RequestCleaner.trimUserRequest(userRequest);
        // Save user
        User userEntity = modelMapper.map(userRequest, User.class);
        User savedUser = userRepository.save(userEntity);
        return modelMapper.map(savedUser, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UserRequest user) {

        String userId = user.getUserId();
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID must not be null or blank");
        }

        User existingUser = userRepository.findByUserIdIgnoreCaseAndIsDeletedFalse(userId.trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        existingUser.setUserName(user.getUserName().trim());
        existingUser.setEmail(user.getEmail().trim());
        existingUser.setPassword(user.getPassword().trim());
        existingUser.setRole(user.getRole().trim());
        // Save updated user
        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserResponse.class);
    }


    @Override
    @Transactional
    public UserResponse deleteUserByUserId(String userId) {

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID must not be null or blank");
        }

        String getUserId = userId.trim().toLowerCase();
        User user = userRepository.findByUserIdIgnoreCaseAndIsDeletedFalse(getUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setIsDeleted(true);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponse.class);
    }

    @Override
    @Transactional
    public List<UserResponse> getAllUser() {
        List<User> getAllUser=this.userRepository.findAllByIsDeletedFalse();
        List<UserResponse> allUserRequest=getAllUser.stream().map((user)-> this.modelMapper.map(user, UserResponse.class)).collect(Collectors.toList());
        return allUserRequest;
    }

    @Override
    @Transactional
    public UserResponse getUserByUserId(String userId) {

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID must not be null or blank");
        }

        String getUserId = userId.trim().toLowerCase();
        User user = userRepository.findByUserIdIgnoreCaseAndIsDeletedFalse(getUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User is not found on server: " + userId));
        return modelMapper.map(user, UserResponse.class);
    }


}
