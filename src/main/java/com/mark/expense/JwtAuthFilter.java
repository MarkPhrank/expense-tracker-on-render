package com.mark.expense;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class JwtAuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        String path = request.getRequestURI();
        
        // 🔓 Публичные эндпоинты (не требуют токена)
        if (path.startsWith("/api/auth") || path.equals("/") || path.startsWith("/static")) {
            chain.doFilter(req, res);
            return;
        }
        
        // 🔐 Проверка токена для защищённых эндпоинтов
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
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
        ((HttpServletResponse) res).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
