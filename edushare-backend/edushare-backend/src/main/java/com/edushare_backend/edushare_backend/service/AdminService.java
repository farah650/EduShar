package com.edushare_backend.edushare_backend.service;

import com.edushare_backend.edushare_backend.entity.*;
import com.edushare_backend.edushare_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final PersonRepository personRepo;
    private final AdminRepository adminRepo;
    private final VideoRepository videoRepo;
    private final TransactionRepository transRepo;
    private final PaymentRepository paymentRepo;
    private final PasswordEncoder passwordEncoder;

    public Admin registerAdmin(Admin admin) {
        if (adminRepo.findByEmail(admin.getEmail()).isPresent()) {
            throw new RuntimeException("Un admin avec cet email existe déjà");
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRole(Role.ADMIN);
        return adminRepo.save(admin);
    }

    public Admin loginAdmin(String email, String password) {
        Admin admin = adminRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin non trouvé"));

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("Mot de passe invalide");
        }

        return admin;
    }

    public List<Person> getAllUsers() {
        return personRepo.findAll();
    }

    public List<Person> searchUsers(String name) {
        return personRepo.findByNameContainingIgnoreCase(name);
    }

    public Person getUserById(Long userId) {
        return personRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @Transactional
    public void blockUser(Long userId) {
        if (!personRepo.existsById(userId)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        personRepo.deleteById(userId);
    }

    public List<Video> getAllVideos() {
        return videoRepo.findAll();
    }

    public List<Video> searchVideos(String query) {
        return videoRepo.findByTitleContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(query, query);
    }

    public Video getVideoById(Long videoId) {
        return videoRepo.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));
    }

    @Transactional
    public void deleteVideo(Long videoId) {
        if (!videoRepo.existsById(videoId)) {
            throw new RuntimeException("Vidéo non trouvée");
        }
        videoRepo.deleteById(videoId);
    }

    public List<Transaction> getAllTransactions() {
        return transRepo.findAll();
    }

    public List<Transaction> getTransactionsByUser(Long userId) {
        return transRepo.findByBuyerId(userId);
    }

    public Transaction getTransactionById(Long transactionId) {
        return transRepo.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée"));
    }

    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = personRepo.count();
        long totalVideos = videoRepo.count();
        long totalTransactions = transRepo.count();
        double totalRevenue = transRepo.findAll().stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        stats.put("totalUsers", totalUsers);
        stats.put("totalVideos", totalVideos);
        stats.put("totalTransactions", totalTransactions);
        stats.put("totalRevenue", totalRevenue);
        stats.put("platformEarnings", totalRevenue * 0.1);

        return stats;
    }

    public Map<String, Object> getDailyStats() {
        Map<String, Object> dailyStats = new HashMap<>();
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime yesterday = today.minusDays(1);

        // Utiliser des méthodes de base si les méthodes spécifiques n'existent pas
        long totalUsers = personRepo.count();
        long totalVideos = videoRepo.count();
        long totalTransactions = transRepo.count();

        dailyStats.put("newUsersToday", totalUsers); // À améliorer avec des dates
        dailyStats.put("newVideosToday", totalVideos);
        dailyStats.put("newTransactionsToday", totalTransactions);
        dailyStats.put("revenueToday", 0.0);

        return dailyStats;
    }

    public Map<String, Object> getUserAnalytics(Long userId) {
        Person user = personRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<Video> userVideos = videoRepo.findByContributorId(userId);
        List<Transaction> userPurchases = transRepo.findByBuyerId(userId);
        List<Transaction> userSales = transRepo.findByVideoContributorId(userId);

        double totalSpent = userPurchases.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalEarned = userSales.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("user", user);
        analytics.put("totalVideosUploaded", userVideos.size());
        analytics.put("totalVideosPurchased", userPurchases.size());
        analytics.put("totalSpent", totalSpent);
        analytics.put("totalEarned", totalEarned);
        analytics.put("currentBalance", user.getCreditBalance());
        analytics.put("joinDate", user.getDateCreation());

        return analytics;
    }

    public boolean userExists(Long userId) {
        return personRepo.existsById(userId);
    }

    public boolean videoExists(Long videoId) {
        return videoRepo.existsById(videoId);
    }

    public void validateUser(Long id) {
        if (!personRepo.existsById(id) && !adminRepo.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
    }

    public long getTotalTransactions() {
        return transRepo.count();
    }

    public double getTotalRevenue() {
        return transRepo.findAll().stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}