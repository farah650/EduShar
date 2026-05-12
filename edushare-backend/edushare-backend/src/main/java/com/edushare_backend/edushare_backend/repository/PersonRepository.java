package com.edushare_backend.edushare_backend.repository;

import com.edushare_backend.edushare_backend.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByEmail(String email);
    List<Person> findAllByOrderByDateCreationDesc();
    List<Person> findByNameContainingIgnoreCase(String name);
    boolean existsByEmail(String email);
    long countByDateCreationAfter(LocalDateTime date);
    List<Person> findTop5ByOrderByUploadedVideosDesc();
}