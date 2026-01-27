package com.practices.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationRequest {

    private String uuid;

    @NotBlank(message = "Education Id must not be blank")
    @Size(max = 20, message = "Education ID must be at most 20 characters")
    private String educationId;

    @NotBlank(message = "Education name must not be blank")
    @Size(max = 20, message = "Education Name must be at most 20 characters")
    private String educationName;

    @NotNull(message = "Date must not be null")
    private LocalDateTime fromDate;

    @NotNull(message = "Date must not be null")
    private LocalDateTime toDate;
}
