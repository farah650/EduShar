package com.edushare_backend.edushare_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("PERSON")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Person extends Utilisateurs {

    private double creditBalance = 0.0;
    private String phone;

    // Vidéos uploadées par cette personne (en tant que contributeur)
    @OneToMany(mappedBy = "contributor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // AJOUTÉ - empêche la sérialisation JSON circulair
    private List<Video> uploadedVideos = new ArrayList<>();

    // Transactions où cette personne est l'acheteur
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // AJOUTÉ
    private List<Transaction> purchases = new ArrayList<>();

    // Paiements effectués par cette personne
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // AJOUTÉ
    private List<Payment> payments = new ArrayList<>();
}