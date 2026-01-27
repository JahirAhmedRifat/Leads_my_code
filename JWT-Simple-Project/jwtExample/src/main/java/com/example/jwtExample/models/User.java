package com.example.jwtExample.models;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.*;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;

@Data
@Entity
@Table(name = "user_table", uniqueConstraints = { @UniqueConstraint(columnNames = { "email" }) })
public class User implements UserDetails {
	
	
//	---------------------- together with UUID & AutoIncrement ---------
//	   @Id
//	    @GeneratedValue(strategy = GenerationType.IDENTITY) // For auto-increment
//	    private Long id;
//
//	    @GeneratedValue(strategy = GenerationType.UUID) // For UUID generation
//	    private UUID uuid;
//	-----------------------------

	@Id
	// @GeneratedValue(strategy = GenerationType.UUID)
	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private String userId; // PK, NOT NULL

	private String name;

	@Column(nullable = false, unique = true)
	private String email; // NOT NULL, UNIQUE
	private String password;
	private String about;
	private String role;

//	@jakarta.persistence.PrePersist
//	public void prePersist() {
//		if (this.userId == null || this.userId.isEmpty()) {
//			this.userId = "U" + UUID.randomUUID().toString();
//		}
//	}

	// Set userId start with 'U' and length 4 digits----
	private static final AtomicInteger counter = new AtomicInteger(1); // Initial ID value
	
	@PrePersist
	public void prePersist() {
		if (this.userId == null || this.userId.isEmpty()) {
			this.userId = "U" + String.format("%04d", counter.getAndIncrement());
		}
	}
		
	public User() {
		super();
	}

	

	public User(String userId, String name, String email, String password, String about, String role) {
		super();
		this.userId = userId;
		this.name = name;
		this.email = email;
		this.password = password;
		this.about = about;
		this.role = role;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public static AtomicInteger getCounter() {
		return counter;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		return email;
	}

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        //return List.of(); // No roles/authorities for now
//        return null;
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//	@Override
//	public String getPassword() {
//		// TODO Auto-generated method stub
//		return password;
//	}
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }

}

