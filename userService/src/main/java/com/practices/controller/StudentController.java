package com.practices.controller;

import com.practices.common.ApiResponse;
import com.practices.common.ApiResponseBuilder;
import com.practices.dto.StudentRequest;
import com.practices.dto.StudentSearchRequest;
import com.practices.dto.response.StudentResponse;
import com.practices.services.IStudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/info/students")
@RequiredArgsConstructor
public class StudentController {

    private final IStudentService studentService;

    // -------- Save Student ----------------
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody StudentRequest request) {
        StudentResponse user=studentService.createStudent(request);
        return ApiResponseBuilder.build("User Created Successfully", true, HttpStatus.CREATED, user);
    }
    // -------- update Student ----------------
    @PutMapping("/update/{studentId}")
    public ResponseEntity<ApiResponse> updateStudent(@PathVariable String studentId,
                                                     @Valid @RequestBody StudentRequest request) {
        StudentResponse student = studentService.updateStudent(studentId, request);
        return ApiResponseBuilder.build("Student updated successfully", true, HttpStatus.OK, student);
    }

    // -------- Get all students with education list -----
    @GetMapping("/show")
    public ResponseEntity<ApiResponse> getAllStudents() {
        List<StudentResponse> students = studentService.getAllStudents();
        return ApiResponseBuilder.build("Students fetched successfully", true, HttpStatus.OK, students);
    }

    // ------- Get single student by studentId -----------
    @GetMapping("/find/{studentId}")
    public ResponseEntity<ApiResponse> getStudentByStudentId(@Valid @PathVariable String studentId) {
        StudentResponse student = studentService.getStudentByStudentId(studentId);
        return ApiResponseBuilder.build("Student fetched successfully", true, HttpStatus.OK, student);
    }

    // ---------- Soft delete student by studentId -----------
    @DeleteMapping("/softDelete/{studentId}")
    public ResponseEntity<ApiResponse> deleteStudent(@Valid @PathVariable String studentId) {
        studentService.deleteStudent(studentId);
        return ApiResponseBuilder.build("Student deleted successfully", true, HttpStatus.OK, null);
    }

    // ---------- Individual search -----------
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchStudents(
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        LocalDateTime from = (fromDate != null) ? fromDate.atStartOfDay() : null;
        LocalDateTime to = (toDate != null) ? toDate.atTime(LocalTime.MAX) : null;

        StudentSearchRequest request = StudentSearchRequest.builder()
                .studentId(studentId)
                .name(name)
                .address(address)
                .gender(gender)
                .fromDate(from)
                .toDate(to)
                .build();

        List<StudentResponse> responses = studentService.searchStudents(request);
        return ApiResponseBuilder.build("Filtered students fetched successfully", true, HttpStatus.OK, responses);

    }

//    ---------- by RequestBody ---------------
//    @PostMapping("/search")
//    public ResponseEntity<ApiResponse> searchStudents(@RequestBody StudentSearchRequest request) {
//        List<StudentResponse> responses = studentService.searchStudents(request);
//        return ApiResponseBuilder.build("Filtered students", true, HttpStatus.OK, responses);
//    }

}
