package com.practices.repositories;

import com.practices.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    boolean existsByStudentIdIgnoreCaseAndIsDeletedFalse(String studentId);
    Optional<Student> findByStudentIdAndIsDeletedFalse(String studentId);
    List<Student> findAllByIsDeletedFalse();


}
