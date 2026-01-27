package com.security.spring_boot_security.services;

import com.security.spring_boot_security.exceptions.ResourceNotFoundException;
import com.security.spring_boot_security.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    List<User> list = new ArrayList<>();

    public UserService() {
        list.add(new User("abc", "abc@gmail.com", "abc"));
        list.add(new User("xyz", "xyz@gmail.com", "xyz"));
    }

    // get all users
    public List<User> getAllUser(){
        return this.list;
    }

    // get single user
    public User getUser(String username){
        return this.list.stream()
                .filter((user)-> user.getUsername().equals(username))
                .findAny()
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: "+ username));
    }

//    public User getUser(String username) {
//        List<User> matchedUsers = this.list.stream()
//                .filter(user -> user.getUsername().equals(username))
//                .collect(Collectors.toList());
//
//        if (matchedUsers.isEmpty()) {
//            throw new ResourceNotFoundException("User not found with username: " + username);
//        }
//        return matchedUsers.get(0);
//    }

    // add new user
    public User addUser(User user){
        this.list.add(user);
        return user;
    }

}
