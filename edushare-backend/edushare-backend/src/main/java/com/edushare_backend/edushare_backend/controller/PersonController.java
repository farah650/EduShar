package com.edushare_backend.edushare_backend.controller;

import com.edushare_backend.edushare_backend.config.JwtUtil;
import com.edushare_backend.edushare_backend.entity.*;
import com.edushare_backend.edushare_backend.repository.PersonRepository;
import com.edushare_backend.edushare_backend.repository.PaymentRepository;
import com.edushare_backend.edushare_backend.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final JwtUtil jwtUtil;
    private final PersonRepository personRepository; // Ajouté
    private final PaymentRepository paymentRepository; // Ajouté

    // === AUTHENTIFICATION ===

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Person person) {
        try {
            return ResponseEntity.ok(personService.register(person));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");
            return ResponseEntity.ok(personService.login(email, password));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === ENDPOINT DE DEBUG: GÉNÉRER UN TOKEN DE TEST ===

    @PostMapping("/generate-test-token/{email}")
    public ResponseEntity<?> generateTestToken(@PathVariable String email) {
        try {
            // Génère le token
            String token = jwtUtil.generateToken(email);

            // Vérifie qu'il est valide
            String extractedEmail = jwtUtil.extractUsername(token);
            boolean isValid = jwtUtil.validateToken(token);

            // Décoder pour debug
            System.out.println("=== TOKEN DEBUG ===");
            System.out.println("Email demandé: " + email);
            System.out.println("Token généré: " + token);
            System.out.println("Email extrait: " + extractedEmail);
            System.out.println("Token valide: " + isValid);
            System.out.println("====================");

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "email", extractedEmail,
                    "valid", isValid,
                    "message", "Token de test généré avec succès"
            ));
        } catch (Exception e) {
            System.out.println("ERREUR génération token: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            String email = jwtUtil.extractUsername(token);
            boolean isValid = jwtUtil.validateToken(token);

            return ResponseEntity.ok(Map.of(
                    "email", email,
                    "valid", isValid,
                    "message", isValid ? "Token valide" : "Token invalide"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === GESTION DES VIDÉOS ===

    @PostMapping("/{id}/upload")
    public ResponseEntity<?> upload(@PathVariable Long id, @RequestBody Video video) {
        try {
            return ResponseEntity.ok(personService.uploadVideo(id, video));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/videos")
    public ResponseEntity<?> getAllVideos() {
        return ResponseEntity.ok(personService.getAllVideos());
    }

    @GetMapping("/videos/{videoId}")
    public ResponseEntity<?> getVideo(@PathVariable Long videoId) {
        try {
            return ResponseEntity.ok(personService.getVideoById(videoId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search/category")
    public ResponseEntity<?> searchByCategory(@RequestParam String category) {
        return ResponseEntity.ok(personService.searchVideosByCategory(category));
    }

    @GetMapping("/search/title")
    public ResponseEntity<?> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(personService.searchVideosByTitle(title));
    }

    @PutMapping("/videos/{videoId}")
    public ResponseEntity<?> updateVideo(@PathVariable Long videoId,
                                         @RequestBody Video video,
                                         @RequestParam Long personId) {
        try {
            return ResponseEntity.ok(personService.updateVideo(videoId, video, personId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/videos/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long videoId, @RequestParam Long personId) {
        try {
            personService.deleteVideo(videoId, personId);
            return ResponseEntity.ok(Map.of("message", "Vidéo supprimée avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/videos/{videoId}/watch")
    public ResponseEntity<?> watchVideo(@PathVariable Long videoId) {
        try {
            return ResponseEntity.ok(personService.incrementViews(videoId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === ACHAT ET CRÉDITS ===

    @PostMapping("/{id}/buy/{videoId}")
    public ResponseEntity<?> buyVideo(@PathVariable Long id, @PathVariable Long videoId) {
        try {
            return ResponseEntity.ok(personService.buyVideo(id, videoId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{personId}/add-credits")
    public ResponseEntity<?> addCredits(@PathVariable Long personId,
                                        @RequestBody CreditRequest request) {
        try {
            return ResponseEntity.ok(personService.addCredits(personId, request.getAmount(), request.getPaymentMethod()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{personId}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long personId,
                                      @RequestBody WithdrawRequest request) {
        try {
            return ResponseEntity.ok(personService.withdrawEarnings(personId, request.getAmount(), request.getWithdrawalMethod()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === STATISTIQUES ===

    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getStats(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(personService.getSalesStats(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === GESTION PROFIL ===

    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(personService.getProfile(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long id,
                                           @RequestBody PersonService.ProfileUpdateRequest request) {
        try {
            return ResponseEntity.ok(personService.updateProfile(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === MES VIDÉOS ET CONTENU ===

    @GetMapping("/{id}/my-videos")
    public ResponseEntity<?> getMyVideos(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(personService.getMyVideos(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/purchased-videos")
    public ResponseEntity<?> getPurchasedVideos(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(personService.getPurchasedVideos(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/videos/popular")
    public ResponseEntity<?> getPopularVideos() {
        return ResponseEntity.ok(personService.getPopularVideos());
    }

    // === TRANSACTIONS ET PAIEMENTS ===

    @GetMapping("/{id}/transactions")
    public ResponseEntity<?> getMyTransactions(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(personService.getMyPurchases(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<?> getMyPayments(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(personService.getMyPayments(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === UTILITAIRES ===

    @GetMapping("/{personId}/videos/{videoId}/is-owner")
    public ResponseEntity<?> isVideoOwner(@PathVariable Long personId, @PathVariable Long videoId) {
        try {
            return ResponseEntity.ok(personService.isVideoOwner(personId, videoId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{personId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long personId) {
        try {
            return ResponseEntity.ok(Map.of("balance", personService.getCreditBalance(personId)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === DEBUG ENDPOINT ===

    @GetMapping("/{id}/payments-debug")
    public ResponseEntity<?> getPaymentsDebug(@PathVariable Long id) {
        try {
            // Teste directement le repository
            boolean userExists = personRepository.existsById(id);
            List<Payment> payments = paymentRepository.findByPerson_IdOrderByDateDesc(id);

            return ResponseEntity.ok(Map.of(
                    "userExists", userExists,
                    "paymentCount", payments.size(),
                    "payments", payments
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === CLASSES DE REQUÊTES ===

    public static class CreditRequest {
        private double amount;
        private String paymentMethod;

        // Getters et Setters
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }

    public static class WithdrawRequest {
        private double amount;
        private String withdrawalMethod;

        // Getters et Setters
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public String getWithdrawalMethod() { return withdrawalMethod; }
        public void setWithdrawalMethod(String withdrawalMethod) { this.withdrawalMethod = withdrawalMethod; }
    }
}