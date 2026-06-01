package com.example.community.comment.repository;

import com.example.community.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPostPostIdOrderByCreatedAtDesc(Long postId);

    List<Comment> findAllByWriterMemberIdOrderByCreatedAtDesc(Long memberId);
}
