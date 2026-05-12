package com.edushare_backend.edushare_backend.repository;

import com.edushare_backend.edushare_backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByVideoIdOrderByCreatedAtDesc(Long videoId);
    List<Comment> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    @Query("SELECT AVG(c.rating) FROM Comment c WHERE c.video.id = :videoId")
    Double getAverageRatingByVideoId(@Param("videoId") Long videoId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.video.id = :videoId")
    Long countByVideoId(@Param("videoId") Long videoId);

    boolean existsByAuthorIdAndVideoId(Long authorId, Long videoId);
}