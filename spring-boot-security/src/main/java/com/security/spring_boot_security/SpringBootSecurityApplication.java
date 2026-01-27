package com.security.spring_boot_security;

import com.security.spring_boot_security.models.Employee;
import com.security.spring_boot_security.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SpringBootSecurityApplication implements CommandLineRunner {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Employee emp1=new Employee();
		emp1.setUsername("jahir");
		emp1.setEmail("123@gmail.com");
		emp1.setPassword(this.passwordEncoder.encode("1234"));
		emp1.setRole("ADMIN");
		this.employeeRepository.save(emp1);

		Employee emp2=new Employee();
		emp2.setUsername("jara");
		emp2.setEmail("abc@gmail.com");
		emp2.setPassword(this.passwordEncoder.encode("123"));
		emp2.setRole("NORMAL");
		this.employeeRepository.save(emp2);

	}
}
