package com.mark.expense;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 🔥 ОТКЛЮЧАЕМ ВСЕ СТАНДАРТНЫЕ МЕХАНИЗМЫ SPRING SECURITY
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable())
            .sessionManagement(session -> session.disable())
            .rememberMe(r -> r.disable())
            .requestCache(cache -> cache.disable())
            
            // 🔥 РАЗРЕШАЕМ ДОСТУП К ПУБЛИЧНЫМ РЕСУРСАМ
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/static/**", 
                               "*.css", "*.js", "*.png", "*.jpg", "*.ico", "*.svg",
                               "/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            
            // 🔥 ДОБАВЛЯЕМ НАШ ФИЛЬТР ПЕРЕД СТАНДАРТНЫМИ
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter();
    }

    // 🔥 ЗАГЛУШКА: отключаем создание дефолтного UserDetailsService
    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {
        return username -> {
            throw new UnsupportedOperationException("JWT only - use /api/auth/login");
        };
    }
}
