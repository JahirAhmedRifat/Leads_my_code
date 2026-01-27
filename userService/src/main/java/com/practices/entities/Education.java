package com.practices.entities;

import com.practices.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "educations")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Education extends BaseEntity {

    @Column(name = "education_id", unique = true, nullable = false, length = 20)
    private String educationId;

    @Column(name = "education_name", nullable = false, length = 20)
    private String educationName;

    @Column(name = "from_date", nullable = false)
    private LocalDateTime fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDateTime toDate;

    // ---- Best practics / set primary key in student_id field --
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // --- if I want  custom studentId set in educations table -----
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "student_id", referencedColumnName = "student_id", nullable = false)
//    private Student student;

}
