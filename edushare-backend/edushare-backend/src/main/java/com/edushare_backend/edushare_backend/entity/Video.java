package com.edushare_backend.edushare_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "videos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private double price = 0.0;
    private String videoUrl;
    private String thumbnailUrl;
    private int views = 0;
    private int likes = 0;
    private int duration = 0; // en secondes

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private VideoStatus status = VideoStatus.ACTIVE;

    // Relation avec le contributeur
    @ManyToOne
    @JoinColumn(name = "contributor_id", nullable = false)
    @JsonBackReference  // OPTIONNEL - alternative à @JsonIgnore sur Person
    private Person contributor;

    // Catégorie de la vidéo
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Commentaires sur la vidéo
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // AJOUTÉ pour éviter référence circulaire
    private List<Comment> comments = new ArrayList<>();

    // Transactions pour cette vidéo
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // AJOUTÉ
    private List<Transaction> transactions = new ArrayList<>();

    public enum VideoStatus {
        ACTIVE, INACTIVE, PENDING, REJECTED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Méthodes utilitaires
    public void incrementViews() {
        this.views++;
    }

    public void incrementLikes() {
        this.likes++;
    }

    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes--;
        }
    }

    public boolean isPurchasable() {
        return this.price > 0 && this.status == VideoStatus.ACTIVE;
    }
}