package com.practices.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseBuilder {
    public static ResponseEntity<ApiResponse> build(String msg, Boolean success, HttpStatus status, Object data) {
        ApiResponse response = ApiResponse.builder()
                .message(msg)
                .success(success)
                .status(status)
                .payload(data)
                .build();
        return new ResponseEntity<>(response, status);
    }
}
