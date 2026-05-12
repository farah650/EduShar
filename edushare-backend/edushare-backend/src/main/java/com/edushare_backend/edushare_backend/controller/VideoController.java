package com.edushare_backend.edushare_backend.controller;
import org.springframework.security.access.prepost.PreAuthorize; // AJOUTE CETTE LIGNE

import com.edushare_backend.edushare_backend.entity.Video;
import com.edushare_backend.edushare_backend.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    // === CRÉATION ET UPLOAD ===

    @PostMapping("/upload/{contributorId}")
    @PreAuthorize("hasAnyRole('CONTRIBUTOR', 'PERSON', 'ADMIN')") // AJOUTE CETTE LIGNE
    public ResponseEntity<?> uploadVideo(
            @PathVariable Long contributorId,
            @RequestBody Video video) {
        try {
            return ResponseEntity.ok(videoService.createVideo(video, contributorId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === CONSULTATION ===

    @GetMapping
    public ResponseEntity<?> getAllVideos() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<?> getVideoById(@PathVariable Long videoId) {
        try {
            return ResponseEntity.ok(videoService.getVideoById(videoId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getVideosByCategory(@PathVariable String category) {
        return ResponseEntity.ok(videoService.getVideosByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchVideos(@RequestParam String keyword) {
        return ResponseEntity.ok(videoService.searchVideos(keyword));
    }

    @GetMapping("/contributor/{contributorId}")
    public ResponseEntity<?> getVideosByContributor(@PathVariable Long contributorId) {
        return ResponseEntity.ok(videoService.getVideosByContributor(contributorId));
    }

    // === VIDÉOS POPULAIRES ET RECOMMANDÉES ===

    @GetMapping("/popular")
    public ResponseEntity<?> getPopularVideos() {
        return ResponseEntity.ok(videoService.getPopularVideos());
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentVideos() {
        return ResponseEntity.ok(videoService.getRecentVideos());
    }

    @GetMapping("/free")
    public ResponseEntity<?> getFreeVideos() {
        return ResponseEntity.ok(videoService.getFreeVideos());
    }

    // === MISE À JOUR ===

    @PutMapping("/{videoId}")
    public ResponseEntity<?> updateVideo(
            @PathVariable Long videoId,
            @RequestBody Video videoDetails,
            @RequestParam Long contributorId) {
        try {
            return ResponseEntity.ok(videoService.updateVideo(videoId, videoDetails, contributorId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === SUPPRESSION ===

    @DeleteMapping("/{videoId}")
    public ResponseEntity<?> deleteVideo(
            @PathVariable Long videoId,
            @RequestParam Long contributorId) {
        try {
            videoService.deleteVideo(videoId, contributorId);
            return ResponseEntity.ok(Map.of("message", "Vidéo supprimée avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === INTERACTIONS ===

    @PostMapping("/{videoId}/watch")
    public ResponseEntity<?> watchVideo(@PathVariable Long videoId) {
        try {
            return ResponseEntity.ok(videoService.incrementViews(videoId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{videoId}/like")
    public ResponseEntity<?> likeVideo(@PathVariable Long videoId) {
        try {
            return ResponseEntity.ok(videoService.likeVideo(videoId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{videoId}/unlike")
    public ResponseEntity<?> unlikeVideo(@PathVariable Long videoId) {
        try {
            return ResponseEntity.ok(videoService.unlikeVideo(videoId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === STATISTIQUES ===

    @GetMapping("/stats/contributor/{contributorId}")
    public ResponseEntity<?> getContributorStats(@PathVariable Long contributorId) {
        try {
            return ResponseEntity.ok(videoService.getVideoStats(contributorId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stats/categories")
    public ResponseEntity<?> getCategoryStats() {
        return ResponseEntity.ok(videoService.getCategoryStats());
    }

    // === UTILITAIRES ===

    @GetMapping("/{videoId}/is-owner/{personId}")
    public ResponseEntity<?> isVideoOwner(
            @PathVariable Long videoId,
            @PathVariable Long personId) {
        try {
            return ResponseEntity.ok(videoService.isVideoOwner(videoId, personId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{videoId}/can-purchase")
    public ResponseEntity<?> canPurchaseVideo(@PathVariable Long videoId) {
        try {
            return ResponseEntity.ok(videoService.canPurchaseVideo(videoId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}