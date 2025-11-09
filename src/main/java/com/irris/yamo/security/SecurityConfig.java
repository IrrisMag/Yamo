package com.irris.yamo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/", "/actuator/health", "/public/**").permitAll()
                        
                        // Endpoints Admin
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "OPERATOR")
                        
                        // Endpoints Operator
                        .requestMatchers("/api/orders/**").hasAnyRole("ADMIN", "OPERATOR", "CUSTOMER")
                        .requestMatchers("/api/processing/**").hasAnyRole("ADMIN", "OPERATOR")
                        
                        // Endpoints Driver
                        .requestMatchers("/api/logistics/tasks/driver/**").hasRole("DRIVER")
                        .requestMatchers("/api/logistics/drivers/**").hasAnyRole("ADMIN", "OPERATOR", "DRIVER")
                        
                        // Endpoints Customer
                        .requestMatchers("/api/addresses/**").hasAnyRole("ADMIN", "OPERATOR", "CUSTOMER")
                        
                        // Tous les autres endpoints nécessitent authentification
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
