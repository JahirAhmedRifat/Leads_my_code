package com.example.jwtToken.commonException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public  class ErrorResponse {
    private int status;
    private String message;
    private boolean success;
}
