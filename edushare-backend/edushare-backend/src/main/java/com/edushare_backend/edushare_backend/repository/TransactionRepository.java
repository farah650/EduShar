package com.edushare_backend.edushare_backend.repository;

import com.edushare_backend.edushare_backend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime; // ← IMPORT MANQUANT
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // === RECHERCHE PAR UTILISATEUR ===
    List<Transaction> findByBuyerId(Long buyerId);
    List<Transaction> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);

    // === RECHERCHE PAR CONTRIBUTEUR ===
    List<Transaction> findByVideoContributorId(Long contributorId);
    List<Transaction> findByVideoContributorIdOrderByCreatedAtDesc(Long contributorId);

    // === STATISTIQUES ===
    long countByBuyerId(Long buyerId);
    long countByVideoContributorId(Long contributorId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.buyer.id = :buyerId")
    Double getTotalSpentByBuyer(@Param("buyerId") Long buyerId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.video.contributor.id = :contributorId")
    Double getTotalEarnedByContributor(@Param("contributorId") Long contributorId);

    // === RECHERCHE TEMPORELLE ===
    List<Transaction> findByCreatedAtAfter(LocalDateTime date);
    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.createdAt BETWEEN :start AND :end")
    Double getRevenueBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // === RECHERCHE PAR STATUT ===
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    List<Transaction> findByBuyerIdAndStatus(Long buyerId, Transaction.TransactionStatus status);

    // === TRANSACTIONS RÉCENTES ===
    List<Transaction> findTop10ByOrderByCreatedAtDesc();

    // === VÉRIFICATIONS ===
    boolean existsByBuyerIdAndVideoId(Long buyerId, Long videoId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.buyer.id = :buyerId AND t.video.id = :videoId AND t.status = 'COMPLETED'")
    long countCompletedPurchases(@Param("buyerId") Long buyerId, @Param("videoId") Long videoId);
    long countByCreatedAtAfter(LocalDateTime date);

    // === STATISTIQUES JOURNALIÈRES ===
    @Query("SELECT FUNCTION('DATE', t.createdAt), SUM(t.amount) FROM Transaction t WHERE t.createdAt BETWEEN :start AND :end GROUP BY FUNCTION('DATE', t.createdAt)")
    List<Object[]> getDailyRevenue(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}