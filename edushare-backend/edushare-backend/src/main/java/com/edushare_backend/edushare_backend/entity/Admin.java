package com.edushare_backend.edushare_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("ADMIN")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends Utilisateurs {

    private String adminLevel = "STANDARD";

    // Constructeur avec tous les paramètres nécessaires
    public Admin(String name, String email, String password) {
        super();
        this.setName(name);
        this.setEmail(email);
        this.setPassword(password);
        this.setRole(Role.ADMIN);
    }
}