package com.example.community.post.repository;

import com.example.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p JOIN FETCH p.writer JOIN FETCH p.board WHERE p.board.boardId = :boardId ORDER BY p.createdAt DESC")
    List<Post> findAllByBoardBoardIdOrderByCreatedAtDesc(@Param("boardId") Long boardId);
}
