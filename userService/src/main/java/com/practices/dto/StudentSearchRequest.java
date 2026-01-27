package com.practices.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSearchRequest {
    private String studentId;
    private String name;
    private String address;
    private String gender;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
