package com.practices.dto;

import com.practices.entities.Education;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRequest {

    @NotBlank(message = "Student Id must not be blank")
    @Size(max = 20, message = "Id must be 20 characters")
    private String studentId;

    @NotBlank(message = "Student name must not be blank")
    @Size(max = 50, message = "Id must be 50 characters")
    private String name;

    private String gender;

    @NotBlank(message = "Address must not be blank")
    @Size(max = 1000, message = "Address must not exceed 1000 characters")
    private String address;

    private List<EducationRequest> educations;

}
