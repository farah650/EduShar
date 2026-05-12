package com.edushare_backend.edushare_backend.controller;

import com.edushare_backend.edushare_backend.entity.Admin;
import com.edushare_backend.edushare_backend.entity.Person;
import com.edushare_backend.edushare_backend.entity.Video;
import com.edushare_backend.edushare_backend.entity.Transaction;
import com.edushare_backend.edushare_backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
        try {
            return ResponseEntity.ok(adminService.registerAdmin(admin));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");
            return ResponseEntity.ok(adminService.loginAdmin(email, password));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(@RequestParam String name) {
        return ResponseEntity.ok(adminService.searchUsers(name));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(adminService.getUserById(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/users/{userId}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long userId) {
        try {
            adminService.blockUser(userId);
            return ResponseEntity.ok(Map.of("message", "Utilisateur bloqué avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/videos")
    public ResponseEntity<?> getAllVideos() {
        return ResponseEntity.ok(adminService.getAllVideos());
    }

    @GetMapping("/videos/search")
    public ResponseEntity<?> searchVideos(@RequestParam String query) {
        return ResponseEntity.ok(adminService.searchVideos(query));
    }

    @GetMapping("/videos/{videoId}")
    public ResponseEntity<?> getVideoById(@PathVariable Long videoId) {
        try {
            return ResponseEntity.ok(adminService.getVideoById(videoId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/videos/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long videoId) {
        try {
            adminService.deleteVideo(videoId);
            return ResponseEntity.ok(Map.of("message", "Vidéo supprimée avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getAllTransactions() {
        return ResponseEntity.ok(adminService.getAllTransactions());
    }

    @GetMapping("/transactions/user/{userId}")
    public ResponseEntity<?> getUserTransactions(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(adminService.getTransactionsByUser(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long transactionId) {
        try {
            return ResponseEntity.ok(adminService.getTransactionById(transactionId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stats/overview")
    public ResponseEntity<?> getAdminStats() {
        return ResponseEntity.ok(adminService.getAdminStats());
    }

    @GetMapping("/stats/daily")
    public ResponseEntity<?> getDailyStats() {
        return ResponseEntity.ok(adminService.getDailyStats());
    }

    @GetMapping("/stats/users/{userId}/analytics")
    public ResponseEntity<?> getUserAnalytics(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(adminService.getUserAnalytics(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/{userId}/exists")
    public ResponseEntity<?> userExists(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("exists", adminService.userExists(userId)));
    }

    @GetMapping("/videos/{videoId}/exists")
    public ResponseEntity<?> videoExists(@PathVariable Long videoId) {
        return ResponseEntity.ok(Map.of("exists", adminService.videoExists(videoId)));
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "Admin API is running"));
    }
}