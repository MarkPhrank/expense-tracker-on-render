package com.mark.expense;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody AuthRequest req) {
        if (req.username == null || req.username.isBlank() || req.password == null || req.password.length() < 6) {
            return Map.of("success", false, "error", "Username ≥3 chars, password ≥6 chars");
        }
        boolean ok = UserManager.register(req.username, req.password);
        if (ok) {
            var userOpt = UserManager.findByUsername(req.username);
            if (userOpt.isPresent()) {
                String token = JwtUtil.generateToken(userOpt.get());
                return Map.of("success", true, "token", token, "username", req.username);
            }
        }
        return Map.of("success", false, "error", "Registration failed (user may exist)");
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody AuthRequest req) {
        var userOpt = UserManager.login(req.username, req.password);
        if (userOpt.isPresent()) {
            String token = JwtUtil.generateToken(userOpt.get());
            return Map.of("success", true, "token", token, "username", userOpt.get().username);
        }
        return Map.of("success", false, "error", "Invalid username or password");
    }

    // Внутренний класс для запроса
    public static class AuthRequest {
        public String username;
        public String password;
    }
}
