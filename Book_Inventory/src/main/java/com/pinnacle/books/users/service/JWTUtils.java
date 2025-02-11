package com.pinnacle.books.users.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.pinnacle.books.users.Users;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Component
public class JWTUtils {

    private final SecretKey key;
    private static final long EXPIRATION_TIME = 86400000L; // 24 hours

    public JWTUtils() {
        String secretString = "843567893696976453275974432697R634976R738467TR678T34865R6834R8763T478378637664538745673865783678548735687R3";
        byte[] keyBytes = Decoders.BASE64.decode(secretString);  // Decode Base64 Secret Key
        this.key = Keys.hmacShaKeyFor(keyBytes);  // Generate SecretKey properly
    }

    // Generate JWT token
    public String generateToken(UserDetails userDetails) {
        if (!(userDetails instanceof Users)) {
            throw new IllegalArgumentException("UserDetails must be an instance of Users to generate token with userId");
        }

        Users user = (Users) userDetails;
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)  // Ensure explicit signature algorithm
                .compact();
    }

    // Generate Refresh Token
    public String generateRefreshToken(HashMap<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)  // Explicit signing
                .compact();
    }

    // Extract userId
    public Long extractUserId(String token) {
        return extractClaims(token, claims -> claims.get("userId", Long.class));
    }

    // Extract username
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    // Extract specific claims
//    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
//        Claims claims = Jwts.parser()
//                .verifyWith(key) // Use verifyWith() instead of setSigningKey()
//                .build()
//                .parseSignedClaims(token)  // Use parseSignedClaims() instead of parseClaimsJws()
//                .getPayload();  // Get payload instead of body
//        return claimsResolver.apply(claims);
//    }
 // Extract specific claims
    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) // Use setSigningKey() instead of verifyWith()
                .build()
                .parseClaimsJws(token)  // Use parseClaimsJws() instead of parseSignedClaims()
                .getBody();  // Get body instead of payload
        return claimsResolver.apply(claims);
    }

    // Validate Token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Check Token Expiry
    public boolean isTokenExpired(String token) {
        Date expiration = extractClaims(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
