package com.moonlight.security;

import com.moonlight.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;


@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${secret-key}")
    private String SECRET_KEY;

    public String extractUsername(String jwtToken) {
        return extractClaims(jwtToken, Claims::getSubject);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaim(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaim(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isValid(String jwt, UserDetails userDetails) {
        final String username = extractClaims(jwt, Claims::getSubject);
        final String email = userDetails.getUsername();
        return (username.equals(email) && !isTokenExpired(jwt));
    }

    private boolean isTokenExpired(String jwt) {
        return extractExp(jwt).before(new Date());
    }

    private Date extractExp(String jwt) {
        return extractClaims(jwt, Claims::getExpiration);
    }

    public String generateJwtToken(User savedUser) {
        return generateToken(new HashMap<>(), savedUser);
    }

    private String generateToken(HashMap<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}