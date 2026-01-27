package com.example.jwtToken.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = "Email must not be empty..")
    private String email;
    @NotBlank(message = "Password must not be empty..")
    private String password;
    @NotBlank(message = "Name must not be empty..")
    private String name;
}
