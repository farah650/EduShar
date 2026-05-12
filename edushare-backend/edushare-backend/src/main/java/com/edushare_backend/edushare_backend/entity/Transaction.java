package com.edushare_backend.edushare_backend.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "buyer_id", nullable = false)
    private Person buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(nullable = false)

    private double amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.COMPLETED;

    private String transactionId; // ID unique de transaction
    private String paymentMethod;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime completedAt;

    // Méthodes utilitaires
    public boolean isRefundable() {
        return this.status == TransactionStatus.COMPLETED &&
                this.createdAt.isAfter(LocalDateTime.now().minusDays(30));
    }

    public void markAsCompleted() {
        this.status = TransactionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void markAsRefunded() {
        this.status = TransactionStatus.REFUNDED;
    }

    public void markAsFailed() {
        this.status = TransactionStatus.FAILED;
    }

    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED,
        CANCELLED
    }
}