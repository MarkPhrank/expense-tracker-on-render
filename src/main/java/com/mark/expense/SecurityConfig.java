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
            // 🔥 ОТКЛЮЧАЕМ ВСЁ ЛИШНЕЕ
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable())
            .sessionManagement(session -> session.disable())
            
            // 🔥 РАЗРЕШАЕМ ВСЁ НА УРОВНЕ SPRING — авторизацию делаем ТОЛЬКО в нашем фильтре
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            
            // 🔥 Наш фильтр будет блокировать/пропускать запросы сам
            .addFilterBefore(jwtAuthFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter();
    }

    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {
        return username -> {
            throw new UnsupportedOperationException("JWT only");
        };
    }
}
