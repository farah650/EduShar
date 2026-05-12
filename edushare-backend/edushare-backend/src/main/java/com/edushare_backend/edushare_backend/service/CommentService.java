package com.edushare_backend.edushare_backend.service;

import com.edushare_backend.edushare_backend.entity.Comment;
import com.edushare_backend.edushare_backend.entity.Person;
import com.edushare_backend.edushare_backend.entity.Transaction;
import com.edushare_backend.edushare_backend.entity.Video;
import com.edushare_backend.edushare_backend.repository.CommentRepository;
import com.edushare_backend.edushare_backend.repository.PersonRepository;
import com.edushare_backend.edushare_backend.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PersonRepository personRepository;
    private final VideoRepository videoRepository;

    public Comment addComment(Long videoId, Long authorId, String content, Integer rating) {
        Person author = personRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));

        // CORRECTION : Utilisez getTransactions() au lieu de getPurchases()
        boolean hasPurchased = video.getTransactions().stream()
                .anyMatch(transaction ->
                        transaction.getBuyer().getId().equals(authorId) &&
                                transaction.getStatus() == Transaction.TransactionStatus.COMPLETED
                );

        boolean isVideoOwner = video.getContributor().getId().equals(authorId);
        boolean isFreeVideo = video.getPrice() == 0;

        if (!isVideoOwner && !isFreeVideo && !hasPurchased) {
            throw new RuntimeException("Vous devez acheter la vidéo pour la commenter");
        }

        Comment comment = Comment.builder()
                .content(content)
                .author(author)
                .video(video)
                .rating(rating != null ? rating : 0)
                .build();

        return commentRepository.save(comment);
    }

    public List<Comment> getVideoComments(Long videoId) {
        return commentRepository.findByVideoIdOrderByCreatedAtDesc(videoId);
    }

    public Comment updateComment(Long commentId, Long authorId, String content, Integer rating) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));

        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("Vous ne pouvez modifier que vos propres commentaires");
        }

        if (content != null) {
            comment.setContent(content);
        }
        if (rating != null) {
            comment.setRating(rating);
        }

        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, Long authorId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));

        // L'auteur ou l'admin peut supprimer
        Person author = personRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        boolean isAuthor = comment.getAuthor().getId().equals(authorId);
        boolean isAdmin = "ADMIN".equals(author.getRole().name()); // Corrigé l'ordre
        boolean isVideoOwner = comment.getVideo().getContributor().getId().equals(authorId);

        if (!isAuthor && !isAdmin && !isVideoOwner) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce commentaire");
        }

        commentRepository.delete(comment);
    }

    public Map<String, Object> getVideoRatingStats(Long videoId) {
        Double averageRating = commentRepository.getAverageRatingByVideoId(videoId);
        Long totalComments = commentRepository.countByVideoId(videoId);

        return Map.of(
                "averageRating", averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0,
                "totalComments", totalComments,
                "videoId", videoId
        );
    }
}