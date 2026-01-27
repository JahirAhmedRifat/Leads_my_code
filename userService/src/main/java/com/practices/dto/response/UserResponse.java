package com.practices.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserResponse {
    private String userId;
    private String userName;
    private String email;
    private String role;
        //----- from base entity --------
    private String uuid;
    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}

