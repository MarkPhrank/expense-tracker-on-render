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
        
        // 🔓 РАЗРЕШАЕМ БЕЗ ПРОВЕРКИ ТОКЕНА:
        if (path.startsWith("/api/auth") ||           // Авторизация
            path.equals("/") || path.endsWith(".html") ||  // Главная страница
            path.startsWith("/static/") ||            // CSS, JS, картинки
            path.endsWith(".css") || path.endsWith(".js") ||
            path.endsWith(".png") || path.endsWith(".ico") ||  // favicon
            path.endsWith(".jpg") || path.endsWith(".svg")) {
            chain.doFilter(req, res);
            return;
        }
        
        // 🔐 Для остальных API проверяем токен
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            if (JwtUtil.validateToken(token)) {
                Integer userId = JwtUtil.getUserIdFromToken(token);
                if (userId != null && UserManager.findById(userId).isPresent()) {
                    request.setAttribute("userId", userId);
                    chain.doFilter(req, res);
                    return;
                }
            }
        }
        
        // ❌ Нет валидного токена
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\":\"Unauthorized\"}");
    }
}
