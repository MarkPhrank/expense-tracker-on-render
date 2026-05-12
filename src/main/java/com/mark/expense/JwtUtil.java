package com.mark.expense;

import io.jsonwebtoken.*;
import java.util.Date;

public class JwtUtil {
    // 🔥 В продакшене храни в переменной окружения!
    private static final String SECRET = "my-super-secret-key-change-me-in-prod-2026!";
    private static final long EXPIRATION = 24 * 60 * 60 * 1000; // 24 часа

    public static String generateToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.id))
                .claim("username", user.username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public static Integer getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();
            return Integer.valueOf(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace("Bearer ", ""));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
