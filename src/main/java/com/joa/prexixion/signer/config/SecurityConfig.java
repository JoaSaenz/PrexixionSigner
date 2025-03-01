package com.joa.prexixion.signer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/css/**").permitAll() // Permitir acceso sin
                                                                                       // autenticación
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Solo ROLE_ADMIN puede acceder a /admin/**
                        .requestMatchers("/files", "/upload").hasAnyRole("USER", "ADMIN") // Usuarios normales y admin
                                                                                          // pueden acceder
                        .anyRequest().authenticated()) // Cualquier otra solicitud requiere autenticación
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/files", true)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout.logoutUrl("/logout"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}