package com.practices.common;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
//@Accessors(chain = true)
@Builder
public class ApiResponse {
    private String message;
    private Boolean success;
    private HttpStatus status;
    private Object payload;
}

//------ for @Builder ------

//ApiResponse res = ApiResponse.builder()
//        .message("Success")
//        .success(true)
//        .status(HttpStatus.OK)
//        .payload(data)
//        .build();

//------- for @Accessors ------

//ApiResponse res = new ApiResponse()
//        .setMessage("Success")
//        .setSuccess(true)
//        .setStatus(HttpStatus.OK)
//        .setPayload(data);


