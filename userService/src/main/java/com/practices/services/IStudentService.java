package com.practices.services;

import com.practices.dto.StudentRequest;
import com.practices.dto.StudentSearchRequest;
import com.practices.dto.response.StudentResponse;
import com.practices.dto.response.UserResponse;

import java.util.List;

public interface IStudentService {
    StudentResponse createStudent(StudentRequest request);
    StudentResponse updateStudent(String studentId, StudentRequest request);
    List<StudentResponse> getAllStudents();
    StudentResponse getStudentByStudentId(String studentId);
    void deleteStudent(String studentId);
    List<StudentResponse> searchStudents(StudentSearchRequest request);

}
