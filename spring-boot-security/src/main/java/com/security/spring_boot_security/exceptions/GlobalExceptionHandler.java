package com.security.spring_boot_security.exceptions;

import com.security.spring_boot_security.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                ex.getMessage(),
                false,
                HttpStatus.NOT_FOUND,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
