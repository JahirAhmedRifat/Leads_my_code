package com.practices.exception;

import com.practices.common.ApiResponse;
import com.practices.common.ApiResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ApiResponse> handlerResourceNotFoundException(ResourceNotFoundException ex){
//        String message =ex.getMessage();
//        Object data = "Data is not found";
//        ApiResponse response = ApiResponse.builder().message(message).success(false).status(HttpStatus.NOT_FOUND).payload(data).build(); // for @Builder
////        ApiResponse response = new ApiResponse().setMessage(message).setSuccess(true).setStatus(HttpStatus.NOT_FOUND).setPayload(data); // for @Accessors
//        return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
//    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ApiResponseBuilder.build(ex.getMessage(), false, HttpStatus.NOT_FOUND, "Data not found");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ApiResponseBuilder.build(ex.getMessage(), false, HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse> handleDuplicateResource(DuplicateResourceException ex) {
        return ApiResponseBuilder.build(ex.getMessage(), false, HttpStatus.BAD_REQUEST, null);
    }
}
