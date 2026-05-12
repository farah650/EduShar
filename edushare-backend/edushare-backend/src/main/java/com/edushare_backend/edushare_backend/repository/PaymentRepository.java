package com.edushare_backend.edushare_backend.repository;

import com.edushare_backend.edushare_backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // CORRECTION: utiliser Person_Id (avec underscore) car la colonne est person_id
    List<Payment> findByPerson_IdOrderByDateDesc(Long personId);

    List<Payment> findByPerson_IdAndTypeOrderByDateDesc(Long personId, Payment.PaymentType type);

    List<Payment> findByStatus(Payment.PaymentStatus status);

    List<Payment> findByDateAfter(LocalDateTime date);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.person.id = :personId AND p.type = 'CREDIT' AND p.status = 'SUCCESS'")
    Double getTotalCreditsByPerson(@Param("personId") Long personId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.person.id = :personId AND p.type = 'WITHDRAWAL' AND p.status = 'SUCCESS'")
    Double getTotalWithdrawalsByPerson(@Param("personId") Long personId);

    List<Payment> findByPerson_IdAndStatusOrderByDateDesc(Long personId, Payment.PaymentStatus status);

    // === QUERY PERSONNALISÉE (alternative) ===
    @Query("SELECT p FROM Payment p WHERE p.person.id = :personId ORDER BY p.date DESC")
    List<Payment> findPaymentsByPersonId(@Param("personId") Long personId);

    // === STATISTIQUES ADMIN ===
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.type = 'CREDIT' AND p.status = 'SUCCESS' AND p.date BETWEEN :start AND :end")
    Double getTotalCreditsBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.type = 'WITHDRAWAL' AND p.status = 'SUCCESS' AND p.date BETWEEN :start AND :end")
    Double getTotalWithdrawalsBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}