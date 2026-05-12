package com.edushare_backend.edushare_backend.service;

import com.edushare_backend.edushare_backend.entity.Utilisateurs;
import com.edushare_backend.edushare_backend.entity.Person;
import com.edushare_backend.edushare_backend.entity.Admin;
import com.edushare_backend.edushare_backend.repository.PersonRepository;
import com.edushare_backend.edushare_backend.repository.AdminRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;
    private final AdminRepository adminRepository;

    public CustomUserDetailsService(PersonRepository personRepository, AdminRepository adminRepository) {
        this.personRepository = personRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateurs utilisateur = findUserByEmail(email);

        // === DEBUG LOGS ===
        System.out.println("=== DEBUG CUSTOM USER DETAILS ===");
        System.out.println("Email: " + email);
        System.out.println("Class: " + utilisateur.getClass().getSimpleName());
        System.out.println("Role from DB: " + utilisateur.getRole());
        System.out.println("Role name: " + utilisateur.getRole().name());

        String role = "ROLE_" + utilisateur.getRole().name();
        System.out.println("Final role string: " + role);
        System.out.println("===============================\n");
        // === FIN DEBUG ===

        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                .build();
    }

    private Utilisateurs findUserByEmail(String email) {
        // Chercher d'abord dans Person
        Optional<Person> person = personRepository.findByEmail(email);
        if (person.isPresent()) {
            System.out.println("User found in Person table");
            return person.get();
        }

        // Si pas trouvé, chercher dans Admin
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            System.out.println("User found in Admin table");
            return admin.get();
        }

        throw new UsernameNotFoundException("Utilisateur non trouvé: " + email);
    }
}