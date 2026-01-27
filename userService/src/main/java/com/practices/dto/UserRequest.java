package com.practices.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserRequest {
    @NotBlank(message = "User ID must not be blank")
    @Size(max = 20, message = "User ID must be at most 20 characters")
    private String userId;

    @NotBlank(message = "User name is required")
    @Size(max = 50, message = "User name must be at most 50 characters")
    private String userName;

    @NotBlank(message = "Email is required")
//    @Email(message = "Invalid email format")
    @Size(max = 50, message = "Email must be at most 50 characters")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String role;

}
