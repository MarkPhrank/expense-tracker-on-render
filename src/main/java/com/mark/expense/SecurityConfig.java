package com.mark.expense;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 🔥 Отключаем стандартный вход Spring Security
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable())
            
            // 🔥 Разрешаем доступ к статике и API авторизации без токена
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/static/**", "/api/auth/**").permitAll()
                .anyRequest().authenticated() // Остальные запросы — только с токеном
            )
            
            // 🔥 Отключаем CSRF для API
            .csrf(csrf -> csrf.disable());
            
        return http.build();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilter() {
        FilterRegistrationBean<JwtAuthFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new JwtAuthFilter());
        reg.addUrlPatterns("/api/*");
        reg.setOrder(1);
        return reg;
    }
}
