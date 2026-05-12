package com.mark.expense;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;

public class JwtUtil {
    // 🔥 Секретный ключ (минимум 32 символа для HS256)
    private static final String SECRET = "my-super-secret-key-change-me-in-prod-2026!";
    private static final long EXPIRATION = 24 * 60 * 60 * 1000; // 24 часа
    
    // 🔥 Создаём ключ правильно для jjwt 0.11.5
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.id))
                .claim("username", user.username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Integer getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(cleanToken(token))
                    .getBody();
            return Integer.valueOf(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(cleanToken(token));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // 🔥 Удаляем "Bearer " и лишние пробелы
    private static String cleanToken(String token) {
        if (token == null) return "";
        return token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();
    }
}
