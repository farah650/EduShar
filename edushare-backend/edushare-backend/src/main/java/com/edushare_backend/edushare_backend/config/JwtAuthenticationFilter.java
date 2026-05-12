package com.edushare_backend.edushare_backend.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();

        // === DEBUG: Affiche la requête ===
        System.out.println("\n=== JWT FILTER DEBUG ===");
        System.out.println("Request URI: " + requestURI);
        System.out.println("Method: " + request.getMethod());
        System.out.println("Authorization Header: " +
                (authorizationHeader != null ? authorizationHeader.substring(0, Math.min(30, authorizationHeader.length())) + "..." : "null"));

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println("Username extracted from token: " + username);
            } catch (Exception e) {
                System.out.println("ERROR extracting username from token: " + e.getMessage());
                logger.warn("JWT token is invalid or expired: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                System.out.println("Loading UserDetails for: " + username);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                System.out.println("UserDetails loaded:");
                System.out.println("  - Username: " + userDetails.getUsername());
                System.out.println("  - Authorities: " + userDetails.getAuthorities());
                System.out.println("  - Password (hashed): " +
                        (userDetails.getPassword() != null ? userDetails.getPassword().substring(0, 20) + "..." : "null"));

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    System.out.println("Token VALID for user: " + username);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    System.out.println("Authentication SET in SecurityContext:");
                    System.out.println("  - Authenticated: " + authToken.isAuthenticated());
                    System.out.println("  - Principal: " + authToken.getPrincipal().getClass().getSimpleName());
                    System.out.println("  - Authorities: " + authToken.getAuthorities());
                } else {
                    System.out.println("Token INVALID for user: " + username);
                }
            } catch (Exception e) {
                System.out.println("ERROR in authentication: " + e.getMessage());
                logger.error("Cannot set user authentication: " + e.getMessage());
            }
        } else if (username == null) {
            System.out.println("No username extracted from token");
        } else {
            System.out.println("Authentication already exists in context");
        }

        System.out.println("=== END JWT FILTER ===\n");

        chain.doFilter(request, response);
    }
}