package com.practices.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationResponse {
    private Long id;
    private String uuid;
    private String educationId;
    private String educationName;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
}
