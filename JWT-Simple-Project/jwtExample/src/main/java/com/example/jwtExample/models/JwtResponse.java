package com.example.jwtExample.models;


public class JwtResponse {

	private String jwtToken;
	private String username;
	
	 private JwtResponse(Builder builder) {
	        this.jwtToken = builder.jwtToken;
	        this.username = builder.username;
	    }

	    public static class Builder {
	        private String jwtToken;
	        private String username;

	        public Builder jwtToken(String jwtToken) {
	            this.jwtToken = jwtToken;
	            return this;
	        }

	        public Builder username(String username) {
	            this.username = username;
	            return this;
	        }

	        public JwtResponse build() {
	            return new JwtResponse(this);
	        }
	    }
	
	
	public JwtResponse() {
		super();
	}

	public JwtResponse(String jwtToken, String username) {
		super();
		this.jwtToken = jwtToken;
		this.username = username;
	}

	public String getJwtToken() {
		return jwtToken;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "JwtResponse [jwtToken=" + jwtToken + ", username=" + username + ", getJwtToken()=" + getJwtToken()
				+ ", getUsername()=" + getUsername() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
		
}
