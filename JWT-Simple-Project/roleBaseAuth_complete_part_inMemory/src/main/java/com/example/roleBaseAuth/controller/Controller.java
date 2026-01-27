package com.example.roleBaseAuth.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.roleBaseAuth.entities.Student;
import com.example.roleBaseAuth.repository.StudentRepo;

@RestController
@RequestMapping("/auth")
public class Controller {

	@Autowired
	private StudentRepo studentRepo;

	// http://localhost:8081/auth/admin/students

	// --------- get all data ------
	@GetMapping("/admin/students")
	public List<Student> getAllStudents() {
		System.out.println("getting students");
		return studentRepo.findAll();
	}

	// --------- insert data ------
	@PostMapping("/admin/add")
	public Student insertData(@RequestBody Student student) {
		System.out.println("adding student");
		return studentRepo.save(student);
	}
	
	// http://localhost:8081/auth/user/dashboard 
	
	@GetMapping("/user/dashboard")
	public String userDashboard() {
		return "Welcome to User Dashboard !!!!!";
	}
	
	@GetMapping("/user/view")
	public String userView() {
		return "Show only user accessed ........";
	}
	
	// http://localhost:8081/auth/public/info
	
	@GetMapping("/public/info")
	public String publicInfo() {
		return "This is public information...";
	}
	
	@PostMapping("/public/view")
	public String publicShow() {
		return "Showing by public...";
	}

}
