package io.github.dziodzi.service;

import io.github.dziodzi.entity.User;
import io.github.dziodzi.tools.LogExecutionTime;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for handling JWT creation, validation, and extraction
 * of claims from tokens, including username, expiration time, and other custom claims.
 */
@Service
@Slf4j
@LogExecutionTime
public class JwtService {
    
    @Value("${custom.tokens.sign-key}")
    private String jwtSigningKey;
    
    /**
     * Extracts the username from a JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Generates a JWT token for a given user, including custom claims.
     * The token expiration time depends on whether the "remember me" flag is set.
     *
     * @param userDetails the user details to include in the token
     * @param rememberMe whether the user chose "remember me" for a longer expiration time
     * @return the generated JWT token
     */
    public String generateToken(UserDetails userDetails, boolean rememberMe) {
        Map<String, Object> claims = getUserClaims(userDetails);
        return createToken(claims, userDetails, rememberMe);
    }
    
    /**
     * Validates the given JWT token by checking the username and expiration.
     *
     * @param token the JWT token to validate
     * @param userDetails the user details to compare with the token
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    
    /**
     * Retrieves the custom claims (e.g., id, email, role) from the user details.
     *
     * @param userDetails the user details to extract claims from
     * @return a map of custom claims
     */
    private Map<String, Object> getUserClaims(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("email", customUserDetails.getEmail());
            claims.put("role", customUserDetails.getRole());
        }
        return claims;
    }
    
    /**
     * Creates a JWT token with the given claims and expiration time.
     *
     * @param claims the claims to include in the token
     * @param userDetails the user details to include in the token
     * @param rememberMe whether the user chose "remember me" for a longer expiration time
     * @return the generated JWT token
     */
    private String createToken(Map<String, Object> claims, UserDetails userDetails, boolean rememberMe) {
        long expirationTime = rememberMe ? 1000L * 60 * 60 * 24 * 30 : 1000L * 60 * 10;
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Extracts a specific claim from the JWT token.
     *
     * @param token the JWT token
     * @param claimsResolver the function used to extract the claim
     * @param <T> the type of the claim
     * @return the extracted claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }
    
    /**
     * Checks if the JWT token has expired.
     *
     * @param token the JWT token to check
     * @return true if the token has expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extracts all claims from the JWT token.
     *
     * @param token the JWT token
     * @return the claims extracted from the token
     * @throws IllegalArgumentException if the token is invalid
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Invalid token: " + e.getMessage());
        }
    }
    
    /**
     * Retrieves the signing key used for signing the JWT token.
     *
     * @return the signing key
     * @throws IllegalArgumentException if the signing key is invalid
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("Signing key must be at least 32 bytes.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
