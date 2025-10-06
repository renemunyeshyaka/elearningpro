package com.elearning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - allow registration and authentication endpoints
                .requestMatchers(
                		"/",
                	    "/index.html",
                	    "/index", 
                	    "/login",
                	    "/register",
                	    "/home",
                	    "/dashboard",
                	    "/api/auth/**",
                	    "/api/users/register",           // General registration endpoint
                	    "/api/users/students/register",  // Student registration
                	    "/api/users/instructors/register", // Instructor registration
                	    "/api/users/admins/register",    // Admin registration (if needed)
                	    "/api/users",                    // Allow POST /api/users
                	    "/api/users/students",           // Allow POST /api/users/students  
                	    "/api/users/instructors",        // Allow POST /api/users/instructors
                	    "/api/users/admins",             // Allow POST /api/users/admins
                	    "/error",
                	    "/favicon.ico",
                	    "/favicon.svg", 
                	    "/static/**",
                	    "/css/**",
                	    "/js/**",
                	    "/images/**",
                	    "/assets/**",
                	    "/**.html",
                	    "/**.js",
                	    "/**.css",
                	    "/**.png",
                	    "/**.jpg",
                	    "/**.jpeg", 
                	    "/**.gif",
                	    "/**.svg",
                	    "/**.ico"
                ).permitAll()
                // Secure all other endpoints
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    // Don't return 401 for HTML requests, redirect to login or serve index.html
                    String acceptHeader = request.getHeader("Accept");
                    if (acceptHeader != null && acceptHeader.contains("text/html")) {
                        response.sendRedirect("/index.html");
                    } else {
                        response.setStatus(401);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Authentication required\"}");
                    }
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"Access denied\"}");
                })
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}