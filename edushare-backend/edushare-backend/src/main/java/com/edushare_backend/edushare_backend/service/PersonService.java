package com.edushare_backend.edushare_backend.service;

import com.edushare_backend.edushare_backend.entity.*;
import com.edushare_backend.edushare_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonService {

    private final PersonRepository personRepo;
    private final VideoRepository videoRepo;
    private final TransactionRepository transRepo;
    private final PaymentRepository paymentRepo;
    private final PasswordEncoder passwordEncoder;
    private final PaymentService paymentService;

    // === INSCRIPTION ET CONNEXION ===

    public Person register(Person person) {
        if (personRepo.findByEmail(person.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole(Role.PERSON);
        person.setCreditBalance(0.0);
        return personRepo.save(person);
    }

    public Person login(String email, String password) {
        Person person = personRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(password, person.getPassword())) {
            throw new RuntimeException("Mot de passe invalide");
        }

        return person;
    }

    // === GESTION DES VIDÉOS ===

    public Video uploadVideo(Long personId, Video video) {
        Person contributor = personRepo.findById(personId)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée"));
        video.setContributor(contributor);
        return videoRepo.save(video);
    }

    public List<Video> getAllVideos() {
        return videoRepo.findAll();
    }

    public List<Video> searchVideosByCategory(String category) {
        // CORRECTION : Utilisez la nouvelle méthode
        return videoRepo.findByCategoryNameContainingIgnoreCase(category);
    }

    public List<Video> searchVideosByTitle(String title) {
        return videoRepo.findByTitleContainingIgnoreCase(title);
    }

    public List<Video> searchVideos(String keyword) {
        return videoRepo.searchByKeyword(keyword);
    }

    public Video getVideoById(Long videoId) {
        return videoRepo.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));
    }

    @Transactional
    public Transaction buyVideo(Long personId, Long videoId) {
        return paymentService.processVideoPurchase(personId, videoId);
    }

    public Video updateVideo(Long videoId, Video videoDetails, Long personId) {
        Video video = videoRepo.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));

        // Vérifier la propriété
        if (!video.getContributor().getId().equals(personId)) {
            throw new RuntimeException("Vous ne pouvez modifier que vos propres vidéos");
        }

        if (videoDetails.getTitle() != null) {
            video.setTitle(videoDetails.getTitle());
        }
        if (videoDetails.getDescription() != null) {
            video.setDescription(videoDetails.getDescription());
        }
        if (videoDetails.getCategory() != null) {
            video.setCategory(videoDetails.getCategory());
        }
        if (videoDetails.getPrice() >= 0) {
            video.setPrice(videoDetails.getPrice());
        }
        if (videoDetails.getVideoUrl() != null) {
            video.setVideoUrl(videoDetails.getVideoUrl());
        }
        if (videoDetails.getThumbnailUrl() != null) {
            video.setThumbnailUrl(videoDetails.getThumbnailUrl());
        }

        return videoRepo.save(video);
    }

    public void deleteVideo(Long videoId, Long personId) {
        Video video = videoRepo.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));

        if (!video.getContributor().getId().equals(personId)) {
            throw new RuntimeException("Vous ne pouvez supprimer que vos propres vidéos");
        }

        videoRepo.deleteById(videoId);
    }

    public Video incrementViews(Long videoId) {
        Video video = videoRepo.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));
        video.setViews(video.getViews() + 1);
        return videoRepo.save(video);
    }

    // === GESTION DES CRÉDITS ET PAIEMENTS ===

    @Transactional
    public Payment addCredits(Long personId, double amount, String paymentMethod) {
        return paymentService.addCredits(personId, amount, paymentMethod);
    }

    @Transactional
    public Payment withdrawEarnings(Long personId, double amount, String withdrawalMethod) {
        return paymentService.withdrawEarnings(personId, amount, withdrawalMethod);
    }

    // === STATISTIQUES ===

    public Map<String, Object> getSalesStats(Long contributorId) {
        List<Video> videos = videoRepo.findByContributorId(contributorId);
        List<Transaction> transactions = transRepo.findByVideoContributorId(contributorId);

        double totalRevenue = transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        long totalViews = videos.stream()
                .mapToLong(Video::getViews)
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalVideos", videos.size());
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalViews", totalViews);
        stats.put("totalSales", transactions.size());
        stats.put("availableBalance", personRepo.findById(contributorId).get().getCreditBalance());

        return stats;
    }

    // === GESTION PROFIL ===

    public Person getProfile(Long personId) {
        return personRepo.findById(personId)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée"));
    }

    public Person updateProfile(Long personId, ProfileUpdateRequest request) {
        Person person = personRepo.findById(personId)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée"));

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            person.setName(request.getName().trim());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim();
            if (!person.getEmail().equals(newEmail) && personRepo.findByEmail(newEmail).isPresent()) {
                throw new RuntimeException("Email déjà utilisé");
            }
            person.setEmail(newEmail);
        }
        if (request.getPhone() != null) {
            person.setPhone(request.getPhone());
        }

        return personRepo.save(person);
    }

    public List<Video> getMyVideos(Long contributorId) {
        return videoRepo.findByContributorId(contributorId);
    }

    public List<Transaction> getMyPurchases(Long buyerId) {
        return paymentService.getUserTransactions(buyerId);
    }

    public List<Payment> getMyPayments(Long personId) {
        return paymentService.getPersonPayments(personId);
    }

    // === MÉTHODES UTILITAIRES ===

    public boolean isVideoOwner(Long personId, Long videoId) {
        Video video = videoRepo.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));
        return video.getContributor().getId().equals(personId);
    }

    public List<Video> getPopularVideos() {
        return videoRepo.findAllByOrderByViewsDesc();
    }

    public List<Video> getRecentVideos() {
        return videoRepo.findAllByOrderByCreatedAtDesc();
    }

    public List<Video> getPurchasedVideos(Long personId) {
        List<Transaction> transactions = paymentService.getUserTransactions(personId);
        return transactions.stream()
                .map(Transaction::getVideo)
                .distinct()
                .collect(Collectors.toList());
    }

    public double getCreditBalance(Long personId) {
        Person person = personRepo.findById(personId)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée"));
        return person.getCreditBalance();
    }

    // === NOUVELLES MÉTHODES UTILITAIRES ===

    public boolean hasPurchasedVideo(Long personId, Long videoId) {
        return paymentService.hasUserPurchasedVideo(personId, videoId);
    }

    public Map<String, Object> getPurchaseStats(Long personId) {
        return paymentService.getUserPurchaseStats(personId);
    }

    public Map<String, Object> getPaymentStats(Long personId) {
        return paymentService.getPaymentStats(personId);
    }

    // === VIDÉOS PAR CATÉGORIE AVEC FILTRES ===

    public List<Video> getVideosByCategoryAndStatus(String category, Video.VideoStatus status) {
        // CORRECTION : Utilisez la nouvelle méthode
        return videoRepo.findByCategoryNameAndStatus(category, status);
    }

    public List<Video> getFreeVideos() {
        return videoRepo.findByPriceAndStatusOrderByCreatedAtDesc(0.0, Video.VideoStatus.ACTIVE);
    }

    public List<Video> getVideosByPriceRange(double minPrice, double maxPrice) {
        return videoRepo.findByPriceBetween(minPrice, maxPrice);
    }

    // === CLASSE INTERNE POUR LA MISE À JOUR DU PROFIL ===

    public static class ProfileUpdateRequest {
        private String name;
        private String email;
        private String phone;

        // Getters et Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}