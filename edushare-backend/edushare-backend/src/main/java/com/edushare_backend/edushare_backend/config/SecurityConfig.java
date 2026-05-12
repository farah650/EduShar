package com.edushare_backend.edushare_backend.config;

import com.edushare_backend.edushare_backend.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ==================== PUBLIC ENDPOINTS ====================
                        // Authentification
                        .requestMatchers("/api/auth/**").permitAll()

                        // Catégories
                        .requestMatchers("/api/categories/**").permitAll()

                        // Vidéos publiques
                        .requestMatchers("/api/videos").permitAll()  // IMPORTANT: ajouté
                        .requestMatchers("/api/videos/public/**").permitAll()
                        .requestMatchers("/api/videos/{id}").permitAll()
                        .requestMatchers("/api/videos/search/**").permitAll()
                        .requestMatchers("/api/videos/popular").permitAll()
                        .requestMatchers("/api/videos/recent").permitAll()
                        .requestMatchers("/api/videos/free").permitAll()
                        .requestMatchers("/api/videos/category/**").permitAll()

                        // Upload de vidéos - RÔLES SPÉCIFIQUES
                        .requestMatchers(HttpMethod.POST, "/api/videos/upload/**")
                        .hasAnyRole("CONTRIBUTOR", "ADMIN", "PERSON")

                        // Person (inscription/connexion uniquement)
                        .requestMatchers("/api/person/register").permitAll()
                        .requestMatchers("/api/person/login").permitAll()

                        // Fichiers statiques
                        .requestMatchers("/uploads/**").permitAll()

                        // Documentation API
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // Health check
                        .requestMatchers("/actuator/health").permitAll()

                        // ==================== TEMPORAIRE POUR TEST ====================
                        .requestMatchers("/api/person/**").permitAll()  // LIGNE AJOUTÉE ICI !

                        // ==================== AUTHENTICATED ENDPOINTS ====================
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001", "http://localhost:8080", "http://localhost:8081"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}