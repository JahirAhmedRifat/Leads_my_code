package com.practices.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {

    private Long id;
    private String uuid;
    private String studentId;
    private String name;
    private String address;
    private String gender;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
    private List<EducationResponse> educations;
}
