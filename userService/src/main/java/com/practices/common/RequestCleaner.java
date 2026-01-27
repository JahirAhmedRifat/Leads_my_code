package com.practices.common;

import com.practices.dto.UserRequest;

public class RequestCleaner {

    public static void trimUserRequest(UserRequest req) {
        if (req.getUserId() != null) req.setUserId(req.getUserId().trim());
        if (req.getUserName() != null) req.setUserName(req.getUserName().trim());
        if (req.getEmail() != null) req.setEmail(req.getEmail().trim());
        if (req.getPassword() != null) req.setPassword(req.getPassword().trim());
        if (req.getRole() != null) req.setRole(req.getRole().trim());
    }
}
