package com.mark.expense;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody AuthRequest req) {
        if (req.username == null || req.password == null || req.username.isBlank()) {
            return Map.of("success", false, "error", "Invalid input");
        }
        boolean ok = UserManager.register(req.username, req.password);
        return ok ? Map.of("success", true) : Map.of("success", false, "error", "User exists");
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody AuthRequest req) {
        var userOpt = UserManager.login(req.username, req.password);
        if (userOpt.isPresent()) {
            String token = JwtUtil.generateToken(userOpt.get());
            return Map.of("success", true, "token", token, "username", userOpt.get().username);
        }
        return Map.of("success", false, "error", "Invalid credentials");
    }

    // Внутренний класс для запроса
    public static class AuthRequest {
        public String username;
        public String password;
    }
}
