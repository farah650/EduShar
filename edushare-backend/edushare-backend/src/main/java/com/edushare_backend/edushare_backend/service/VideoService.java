package com.edushare_backend.edushare_backend.service;

import com.edushare_backend.edushare_backend.entity.Video;
import com.edushare_backend.edushare_backend.entity.Person;
import com.edushare_backend.edushare_backend.entity.Category;
import com.edushare_backend.edushare_backend.repository.VideoRepository;
import com.edushare_backend.edushare_backend.repository.PersonRepository;
import com.edushare_backend.edushare_backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoService {

    private final VideoRepository videoRepository;
    private final PersonRepository personRepository;
    private final CategoryRepository categoryRepository; // Ajouté

    // === CRÉATION ET MISE À JOUR ===

    public Video createVideo(Video video, Long contributorId) {
        Person contributor = personRepository.findById(contributorId)
                .orElseThrow(() -> new RuntimeException("Contributeur non trouvé"));

        video.setContributor(contributor);
        return videoRepository.save(video);
    }

    public Video updateVideo(Long videoId, Video videoDetails, Long contributorId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));

        if (!video.getContributor().getId().equals(contributorId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier cette vidéo");
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
        if (videoDetails.getStatus() != null) {
            video.setStatus(videoDetails.getStatus());
        }

        return videoRepository.save(video);
    }

    // === SUPPRESSION ===

    public void deleteVideo(Long videoId, Long contributorId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));

        if (!video.getContributor().getId().equals(contributorId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer cette vidéo");
        }

        videoRepository.delete(video);
    }

    // === CONSULTATION ===

    @Transactional(readOnly = true)
    public List<Video> getAllVideos() {
        return videoRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Video getVideoById(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));
    }

    @Transactional(readOnly = true)
    public List<Video> getVideosByCategory(String categoryName) {
        // CORRECTION : Utilisez findByCategoryNameAndStatus au lieu de findByCategoryAndStatus
        return videoRepository.findByCategoryNameAndStatus(categoryName, Video.VideoStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Video> searchVideos(String keyword) {
        return videoRepository.searchByKeyword(keyword);
    }

    @Transactional(readOnly = true)
    public List<Video> getVideosByContributor(Long contributorId) {
        return videoRepository.findByContributorId(contributorId);
    }

    // === INTERACTIONS ===

    public Video incrementViews(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));
        video.incrementViews();
        return videoRepository.save(video);
    }

    public Video likeVideo(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));
        video.incrementLikes();
        return videoRepository.save(video);
    }

    public Video unlikeVideo(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));
        video.decrementLikes();
        return videoRepository.save(video);
    }

    // === VIDÉOS POPULAIRES ET RECOMMANDÉES ===

    @Transactional(readOnly = true)
    public List<Video> getPopularVideos() {
        return videoRepository.findTop10ByOrderByViewsDesc();
    }

    @Transactional(readOnly = true)
    public List<Video> getRecentVideos() {
        return videoRepository.findTop10ByStatusOrderByCreatedAtDesc(Video.VideoStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Video> getFreeVideos() {
        return videoRepository.findByPriceAndStatusOrderByCreatedAtDesc(0.0, Video.VideoStatus.ACTIVE);
    }

    // === STATISTIQUES ===

    @Transactional(readOnly = true)
    public Map<String, Object> getVideoStats(Long contributorId) {
        List<Video> videos = videoRepository.findByContributorId(contributorId);

        long totalViews = videos.stream().mapToLong(Video::getViews).sum();
        long totalLikes = videos.stream().mapToLong(Video::getLikes).sum();
        double totalRevenue = videos.stream()
                .mapToDouble(v -> v.getPrice() * v.getViews())
                .sum();

        return Map.of(
                "totalVideos", videos.size(),
                "totalViews", totalViews,
                "totalLikes", totalLikes,
                "totalRevenue", totalRevenue,
                "averageViews", videos.isEmpty() ? 0 : totalViews / videos.size()
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getCategoryStats() {
        List<Object[]> results = videoRepository.getVideoCountByCategory();
        return results.stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0], // Le nom de la catégorie
                        obj -> (Long) obj[1]    // Le nombre de vidéos
                ));
    }

    // === MÉTHODES UTILITAIRES ===

    @Transactional(readOnly = true)
    public List<Video> getVideosByCategoryId(Long categoryId) {
        return videoRepository.findByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public List<Video> getTrendingVideos() {
        return videoRepository.findTrendingVideos();
    }

    @Transactional(readOnly = true)
    public List<Video> getVideosByPriceRange(double minPrice, double maxPrice) {
        return videoRepository.findByPriceBetween(minPrice, maxPrice);
    }

    // === VALIDATION ===

    public boolean isVideoOwner(Long videoId, Long personId) {
        return videoRepository.existsByIdAndContributorId(videoId, personId);
    }

    public boolean canPurchaseVideo(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));
        return video.isPurchasable();
    }
}