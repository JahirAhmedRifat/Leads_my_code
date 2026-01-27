package com.example.jwtExample.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.jwtExample.models.User;

public interface UserRepository extends JpaRepository<User, String> {
 
	public Optional<User> findByEmail(String email);
		
	//public Optional<User> findByEmailAndPassword(String email, String password);
		
//	@Query("SELECT u FROM user_table u WHERE u.email = :email AND u.password = :password")
//	public  Optional<User> getData(@Param("email") String email, @Param("password") String password);

	public boolean existsByEmail(String email);
	
}
