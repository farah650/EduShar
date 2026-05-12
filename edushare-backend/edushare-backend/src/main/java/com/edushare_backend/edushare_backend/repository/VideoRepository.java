package com.edushare_backend.edushare_backend.repository;

import com.edushare_backend.edushare_backend.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    // === RECHERCHE PAR CATÉGORIE (MAINTENANT OBJET) ===
    List<Video> findByCategoryId(Long categoryId);
    List<Video> findByCategoryName(String categoryName);
    List<Video> findByCategoryNameContainingIgnoreCase(String categoryName);

    // === RECHERCHE PAR CONTRIBUTEUR ===
    List<Video> findByContributorId(Long contributorId);

    // === RECHERCHE PAR TITRE ===
    List<Video> findByTitleContainingIgnoreCase(String title);

    // === RECHERCHE COMBINÉE ===
    // === TRI ET FILTRES ===
    List<Video> findAllByOrderByCreatedAtDesc();
    List<Video> findByCategoryNameOrderByCreatedAtDesc(String categoryName);
    List<Video> findByStatusOrderByCreatedAtDesc(Video.VideoStatus status);
    List<Video> findByCategoryNameAndStatus(String categoryName, Video.VideoStatus status);

    // === STATISTIQUES ===
    long countByCategoryId(Long categoryId);
    long countByCategoryName(String categoryName);
    long countByContributorId(Long contributorId);
    long countByStatus(Video.VideoStatus status);

    // === RECHERCHE AVANCÉE (corrigée) ===
    @Query("SELECT v FROM Video v WHERE " +
            "LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Video> searchByKeyword(@Param("keyword") String keyword);

    // === VIDÉOS POPULAIRES ===
    List<Video> findTop10ByOrderByViewsDesc();
    List<Video> findTop5ByCategoryNameOrderByViewsDesc(String categoryName);
    List<Video> findTop10ByStatusOrderByViewsDesc(Video.VideoStatus status);

    // === VIDÉOS RÉCENTES ===
    List<Video> findTop10ByStatusOrderByCreatedAtDesc(Video.VideoStatus status);

    // === VÉRIFICATIONS ===
    boolean existsByTitleAndContributorId(String title, Long contributorId);
    boolean existsByIdAndContributorId(Long videoId, Long contributorId);

    // === RECHERCHE PAR PRIX ===
    List<Video> findByPriceBetween(double minPrice, double maxPrice);
    List<Video> findByPriceLessThanEqual(double maxPrice);
    List<Video> findByPriceGreaterThanEqual(double minPrice);

    // === VIDÉOS ACTIVES ===
    List<Video> findByStatus(Video.VideoStatus status);
    List<Video> findByStatusAndContributorId(Video.VideoStatus status, Long contributorId);

    // === MÉTRICS ET ANALYTICS ===
    @Query("SELECT SUM(v.views) FROM Video v WHERE v.contributor.id = :contributorId")
    Long getTotalViewsByContributor(@Param("contributorId") Long contributorId);

    @Query("SELECT SUM(v.likes) FROM Video v WHERE v.contributor.id = :contributorId")
    Long getTotalLikesByContributor(@Param("contributorId") Long contributorId);

    @Query("SELECT AVG(v.price) FROM Video v")
    Double getAverageVideoPrice();

    @Query("SELECT v.category, COUNT(v) FROM Video v GROUP BY v.category ORDER BY COUNT(v) DESC")
    List<Object[]> getVideoCountByCategory();

    // === RECHERCHE TEMPORELLE ===
    List<Video> findByCreatedAtAfter(LocalDateTime date);
    List<Video> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    long countByCreatedAtAfter(LocalDateTime date);

    // === VIDÉOS GRATUITES ===
    List<Video> findByPriceOrderByViewsDesc(double price);
    List<Video> findByPriceAndStatusOrderByCreatedAtDesc(double price, Video.VideoStatus status);

    // === VIDÉOS PAR CONTRIBUTEUR AVEC STATISTIQUES ===
    @Query("SELECT v FROM Video v WHERE v.contributor.id = :contributorId ORDER BY v.views DESC")
    List<Video> findByContributorIdOrderByViewsDesc(@Param("contributorId") Long contributorId);

    // === VIDÉO PAR URL ===
    Optional<Video> findByVideoUrl(String videoUrl);

    // === VIDÉOS POPULAIRES (toutes) ===
    List<Video> findAllByOrderByViewsDesc();

    // === RECHERCHE AVANCÉE (finale) ===
    @Query("SELECT v FROM Video v WHERE " +
            "LOWER(v.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.category.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Video> advancedSearch(@Param("query") String query);

    @Query("SELECT v FROM Video v WHERE v.price = 0 AND v.status = 'ACTIVE'")
    List<Video> findFreeVideos();

    @Query("SELECT v FROM Video v WHERE v.status = 'ACTIVE' ORDER BY v.views DESC LIMIT 10")
    List<Video> findTrendingVideos();

    // === VIDÉOS PAR CATÉGORIE ET STATUT ===
    List<Video> findByCategoryIdAndStatus(Long categoryId, Video.VideoStatus status);

    // === VIDÉOS PAR CONTRIBUTEUR ET CATÉGORIE ===
    List<Video> findByContributorIdAndCategoryId(Long contributorId, Long categoryId);
    @Query("SELECT v FROM Video v WHERE " +
            "LOWER(v.title) LIKE LOWER(CONCAT('%', :title, '%')) OR " +
            "LOWER(v.category.name) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Video> findByTitleContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(
            @Param("title") String title,
            @Param("category") String category
    );

}