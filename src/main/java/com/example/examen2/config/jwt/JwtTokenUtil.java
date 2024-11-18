package com.example.examen2.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String getUsernameFromToken(String token) {
        String username = getClaimFromToken(token, Claims::getSubject);
        System.out.println("Extracted username from token: " + username);
        return username;
    }

    public Date getIssuedAtDateFromToken(String token) {
        Date issuedAt = getClaimFromToken(token, Claims::getIssuedAt);
        System.out.println("Extracted issuedAt date from token: " + issuedAt);
        return issuedAt;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expirationDate = getClaimFromToken(token, Claims::getExpiration);
        System.out.println("Extracted expiration date from token: " + expirationDate);
        return expirationDate;
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        System.out.println("Extracted claims from token: " + claims);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        System.out.println("Parsed all claims: " + claims);
        return claims;
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        boolean isExpired = expiration.before(new Date());
        System.out.println("Is token expired? " + isExpired);
        return isExpired;
    }

    public String generateToken(UserDetails userDetails, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Usar "role" consistentemente
        System.out.println("Generating token with role: " + role);
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        System.out.println("Generated token: " + token);
        return token;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        System.out.println("Is token valid? " + isValid);
        return isValid;
    }

    public String getRoleFromToken(String token) {
        String role = getClaimFromToken(token, claims -> claims.get("role", String.class));
        System.out.println("Extracted role from token: " + role);
        return role;
    }
}
