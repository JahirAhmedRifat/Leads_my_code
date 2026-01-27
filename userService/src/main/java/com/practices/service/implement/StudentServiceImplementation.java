package com.practices.service.implement;

import com.practices.dto.EducationRequest;
import com.practices.dto.StudentRequest;
import com.practices.dto.StudentSearchRequest;
import com.practices.dto.response.EducationResponse;
import com.practices.dto.response.StudentResponse;
import com.practices.entities.Education;
import com.practices.entities.Student;
import com.practices.exception.DuplicateResourceException;
import com.practices.exception.ResourceNotFoundException;
import com.practices.repositories.StudentRepository;
import com.practices.services.IStudentService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentServiceImplementation implements IStudentService {

    private final StudentRepository studentRepository;
    public StudentServiceImplementation(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

//    @Autowired
//    private ModelMapper modelMapper;

    // ------------- Save data -----------
    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {

        // Trim inputs safely
        String studentId = request.getStudentId().trim();
        String name = request.getName().trim();
        String address = request.getAddress().trim();
        String gender = request.getGender().trim();

        // -------- Duplicate check  ---------
        String userIdToCheck = studentId.toLowerCase();
        if (studentRepository.existsByStudentIdIgnoreCaseAndIsDeletedFalse(userIdToCheck)) {
            throw new DuplicateResourceException("Student Id already exists :" + studentId );
        }
        // Build Student entity
        Student student = Student.builder()
                .studentId(studentId)
                .name(name)
                .address(address)
                .gender(gender)
                .build();

        // Build Education list and link to student
        List<Education> educationList = new ArrayList<>();
        if (request.getEducations() != null && !request.getEducations().isEmpty()) {
            for (EducationRequest eduReq : request.getEducations()) {

                Education education = Education.builder()
                        .educationId(eduReq.getEducationId().trim())
                        .educationName(eduReq.getEducationName().trim())
                        .fromDate(eduReq.getFromDate())
                        .toDate(eduReq.getToDate())
                        .student(student)
                        .build();

                educationList.add(education);
            }
        }

        student.setEducationList(educationList);

        // Save to DB
        Student savedStudent = studentRepository.save(student);

        // Map to response
        return mapToResponse(savedStudent);
    }

    // ------------- Update student data -----------
    @Override
    @Transactional
    public StudentResponse updateStudent(String studentId, StudentRequest request) {
        Student student = studentRepository.findByStudentIdAndIsDeletedFalse(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        // Step 1: Update student info
        student.setName(request.getName().trim());
        student.setAddress(request.getAddress().trim());
        student.setGender(request.getGender().trim());

        // Step 2: Prepare old education list map
        List<Education> oldEducations = student.getEducationList();
        Map<String, Education> oldEducationMap = oldEducations.stream()
                .filter(e -> !e.getIsDeleted())
                .collect(Collectors.toMap(Education::getUuid, Function.identity()));

        Set<String> incomingUUIDs = new HashSet<>();
        Set<String> incomingEducationIds = new HashSet<>();

        for (EducationRequest eduReq : request.getEducations()) {
            String trimmedEduId = eduReq.getEducationId().trim();

            // Step 3: Check duplicate in incoming request list
            if (!incomingEducationIds.add(trimmedEduId)) {
                throw new IllegalArgumentException("Duplicate educationId in request: " + trimmedEduId);
            }

            if (eduReq.getUuid() != null && !eduReq.getUuid().isBlank()) {
                // Step 4: Update existing education
                Education existingEdu = oldEducationMap.get(eduReq.getUuid());
                if (existingEdu == null) {
                    throw new ResourceNotFoundException("Education not found with UUID: " + eduReq.getUuid());
                }

                // Check if new educationId conflicts with other old records
                boolean isDuplicateInOld = oldEducations.stream()
                        .anyMatch(e -> !e.getIsDeleted()
                                && !e.getUuid().equals(eduReq.getUuid())
                                && e.getEducationId().equalsIgnoreCase(trimmedEduId));

                if (isDuplicateInOld) {
                    throw new IllegalArgumentException("Education ID already exists: " + trimmedEduId);
                }

                existingEdu.setEducationId(trimmedEduId);
                existingEdu.setEducationName(eduReq.getEducationName().trim());
                existingEdu.setFromDate(eduReq.getFromDate());
                existingEdu.setToDate(eduReq.getToDate());
                existingEdu.setIsDeleted(false);

                incomingUUIDs.add(eduReq.getUuid());
            } else {
                // Step 5: Check duplicate against old records before adding new
                boolean isDuplicateInOld = oldEducations.stream()
                        .anyMatch(e -> !e.getIsDeleted()
                                && e.getEducationId().equalsIgnoreCase(trimmedEduId));

                if (isDuplicateInOld) {
                    throw new IllegalArgumentException("Education ID already exists: " + trimmedEduId);
                }

                // Step 6: Create new Education
                Education newEdu = Education.builder()
                        .educationId(trimmedEduId)
                        .educationName(eduReq.getEducationName().trim())
                        .fromDate(eduReq.getFromDate())
                        .toDate(eduReq.getToDate())
                        .isDeleted(false)
                        .student(student)
                        .build();

                student.getEducationList().add(newEdu);
            }
        }

        // Step 7: Soft delete old educations not in request
        for (Education oldEdu : oldEducations) {
            if (!incomingUUIDs.contains(oldEdu.getUuid())) {
                oldEdu.setIsDeleted(true);
            }
        }

        // Step 8: Save and return response
        return mapToResponse(studentRepository.save(student));
    }

    //------------- Get all data -----------
    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        List<Student> students = studentRepository.findAllByIsDeletedFalse();
        return students.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //------------- Get data by studentId -----------
    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentByStudentId(String studentId) {
        Student student = studentRepository.findByStudentIdAndIsDeletedFalse(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return mapToResponse(student);
    }

    //------------- Soft Delete by studentId -----------
    @Override
    @Transactional
    public void deleteStudent(String studentId) {
        Student student = studentRepository.findByStudentIdAndIsDeletedFalse(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        student.setIsDeleted(true);
        student.setModifiedDate(LocalDateTime.now());

        if (student.getEducationList() != null) {
            for (Education education : student.getEducationList()) {
                education.setIsDeleted(true);
                education.setModifiedDate(LocalDateTime.now());
            }
        }

        studentRepository.save(student);

    }

    // ----------- for individual search -----------------
    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> searchStudents(StudentSearchRequest request) {
        List<Student> students = studentRepository.findAll((Specification<Student>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getStudentId() != null && !request.getStudentId().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("studentId")), request.getStudentId().toLowerCase()));
            }

            if (request.getName() != null && !request.getName().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));
            }

            if (request.getAddress() != null && !request.getAddress().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("address")), "%" + request.getAddress().toLowerCase() + "%"));
            }

            if (request.getGender() != null && !request.getGender().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("gender")), request.getGender().toLowerCase()));
            }

            // Join with education for date filter
            if (request.getFromDate() != null || request.getToDate() != null) {
                Join<Object, Object> educationJoin = root.join("educationList", JoinType.LEFT);
                predicates.add(cb.isFalse(educationJoin.get("isDeleted")));

                if (request.getFromDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(educationJoin.get("fromDate"), request.getFromDate()));
                }

                if (request.getToDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(educationJoin.get("toDate"), request.getToDate()));
                }

                query.distinct(true);
            }

            // Only isDeleted = false students
            predicates.add(cb.isFalse(root.get("isDeleted")));

            return cb.and(predicates.toArray(new Predicate[0]));
        });

        return students.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private StudentResponse mapToResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .uuid(student.getUuid())
                .studentId(student.getStudentId())
                .name(student.getName())
                .address(student.getAddress())
                .gender(student.getGender())
                .isDeleted(student.getIsDeleted())
                .educations(
                        student.getEducationList().stream()
                                .filter(edu -> !edu.getIsDeleted()) // ✅ শুধু isDeleted = false
                                .map(edu -> EducationResponse.builder()
                                        .id(edu.getId())
                                        .uuid(edu.getUuid())
                                        .educationId(edu.getEducationId())
                                        .educationName(edu.getEducationName())
                                        .fromDate(edu.getFromDate())
                                        .toDate(edu.getToDate())
                                        .isDeleted(edu.getIsDeleted())
                                        .build()
                                )
                                .toList()
                )
                .build();
    }


}
