package com.edushare_backend.edushare_backend.repository;

import com.edushare_backend.edushare_backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT c FROM Category c ORDER BY c.name ASC")
    List<Category> findAllOrderByName();

    @Query("SELECT c.name, COUNT(v) FROM Category c LEFT JOIN c.videos v GROUP BY c.id, c.name ORDER BY COUNT(v) DESC")
    List<Object[]> findCategoriesWithVideoCount();
}