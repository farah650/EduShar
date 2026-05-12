package com.edushare_backend.edushare_backend.controller;

import com.edushare_backend.edushare_backend.config.JwtUtil;
import com.edushare_backend.edushare_backend.entity.Person;
import com.edushare_backend.edushare_backend.service.PersonService;
import com.edushare_backend.edushare_backend.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final PersonService personService;

    @PostMapping("/person/register")
    public ResponseEntity<?> registerPerson(@RequestBody Person person) {
        try {
            Person savedPerson = personService.register(person);
            String token = jwtUtil.generateToken(savedPerson.getEmail());

            return ResponseEntity.ok(Map.of(
                    "message", "Inscription réussie",
                    "user", savedPerson,
                    "token", token
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/person/login")
    public ResponseEntity<?> loginPerson(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            // Authentifier l'utilisateur
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            // Charger les détails et générer le token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            final String token = jwtUtil.generateToken(userDetails.getUsername());

            // Récupérer les données personnelles
            Person person = personService.login(email, password);

            return ResponseEntity.ok(Map.of(
                    "message", "Connexion réussie",
                    "user", person,
                    "token", token
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email ou mot de passe incorrect"));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            if (jwtUtil.validateToken(jwt)) {
                String username = jwtUtil.extractUsername(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                return ResponseEntity.ok(Map.of(
                        "valid", true,
                        "username", username
                ));
            }
            return ResponseEntity.ok(Map.of("valid", false));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }
}