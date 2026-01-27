package com.example.jwtExample.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.jwtExample.models.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtHelper {

	// requirement :
	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	// public static final long JWT_TOKEN_VALIDITY = 60;
	private String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

//	@Value("${jwt.secret}")
//  private String secretKey;
//
//  @Value("${jwt.expiration}") // in milliseconds (e.g., 86400000 = 1 day)
//  private long jwtExpiration;

	// retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		// Convert the secret key to a Key object
		Key signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

		return Jwts.parserBuilder().setSigningKey(signingKey) // Use the Key object
				.build().parseClaimsJws(token).getBody();
	}

	// check if the token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

// Generate token
//  public String generateToken(String username) {
//      return Jwts.builder()
//              .setSubject(username)
//              .setIssuedAt(new Date(System.currentTimeMillis()))
//              .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
//              .signWith(SignatureAlgorithm.HS256, secretKey)
//              .compact();
//  }

	// generate token for user by email & password
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, userDetails.getUsername());
	}

	private String doGenerateToken(Map<String, Object> claims, String username) {
		// create Key from Secret
		Key signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

		return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(signingKey, SignatureAlgorithm.HS512).compact();
	}

	// validate token signWith(SignatureAlgorithm.HS512, secret)
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

//  // Get all claims
//  private Claims getClaims(String token) {
//      return Jwts.parser()
//              .setSigningKey(secretKey)
//              .parseClaimsJws(token)
//              .getBody();
//  }
}
