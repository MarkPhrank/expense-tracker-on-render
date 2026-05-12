package com.mark.expense;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class JwtAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // 🔓 ПУБЛИЧНЫЕ ЭНДПОИНТЫ — пропускаем ВСЕГДА
        if (path.startsWith("/api/auth") || 
            path.equals("/") || 
            path.endsWith(".html") || 
            path.startsWith("/static/") ||
            path.endsWith(".css") || 
            path.endsWith(".js") || 
            path.endsWith(".ico") ||
            path.endsWith(".png") || 
            path.endsWith(".jpg") || 
            path.endsWith(".svg")) {
            chain.doFilter(req, res);
            return;
        }
        
        // 🔐 ЗАЩИЩЁННЫЕ ЭНДПОИНТЫ (/api/*) — проверяем токен
        String authHeader = request.getHeader("Authorization");
        System.out.println("🔐 Filter: " + method + " " + path + " | Auth: " + (authHeader != null ? "YES" : "NO"));
        
        // Если токена нет — блокируем
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ Filter: No valid Bearer token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Missing or invalid token\"}");
            return;
        }
        
        // Извлекаем и валидируем токен
        String token = authHeader.substring(7).trim();
        System.out.println("🔐 Filter: Token length=" + token.length());
        
        if (!JwtUtil.validateToken(token)) {
            System.out.println("❌ Filter: Token validation FAILED");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Invalid token\"}");
            return;
        }
        
        // Получаем userId и проверяем пользователя
        Integer userId = JwtUtil.getUserIdFromToken(token);
        System.out.println("🔐 Filter: userId from token=" + userId);
        
        if (userId == null || !UserManager.findById(userId).isPresent()) {
            System.out.println("❌ Filter: User not found for id=" + userId);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"User not found\"}");
            return;
        }
        
        // ✅ Всё ок — передаём userId в запрос и пропускаем дальше
        request.setAttribute("userId", userId);
        System.out.println("✅ Filter: Allowed request for userId=" + userId);
        chain.doFilter(req, res);
    }
}
