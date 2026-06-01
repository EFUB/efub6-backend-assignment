package com.example.community.comment.repository;

import com.example.community.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.writer JOIN FETCH c.post WHERE c.post.postId = :postId ORDER BY c.createdAt DESC")
    List<Comment> findAllByPostPostIdOrderByCreatedAtDesc(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.post JOIN FETCH c.writer WHERE c.writer.memberId = :memberId ORDER BY c.createdAt DESC")
    List<Comment> findAllByWriterMemberIdOrderByCreatedAtDesc(@Param("memberId") Long memberId);
}
