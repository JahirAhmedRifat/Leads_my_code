package com.example.roleBaseAuth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.roleBaseAuth.entities.Student;

public interface StudentRepo extends JpaRepository<Student, Integer> {

}
