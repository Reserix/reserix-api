package com.reserix.api.config;

import com.reserix.api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/uploads/**",
                                "/auth/login",
                                "/auth/register",
                                "/actuator/health",
                                "/actuator/prometheus"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/movies/**"
                        ).permitAll()
                        .requestMatchers(
                                "/api/v1/users/**",
                                "/api/v1/theaters",
                                "/api/v1/theaters/**",
                                "/api/v1/movies"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/screenings",
                                "/api/v1/screenings/**",
                                "/api/v1/rooms"
                        ).hasAnyRole("ADMIN", "THEATER_MANAGER")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/screenings/**",
                                "/api/v1/movies/**",
                                "/api/v1/rooms/**"
                        ).hasAnyRole("ADMIN", "THEATER_MANAGER", "USER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
