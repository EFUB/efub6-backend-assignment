package com.example.community.post.repository;

import com.example.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {

    List<Post> findAllByBoard_BoardIdOrderByCreatedAtDesc(Long boardId);

    long countByBoard_BoardId(Long boardId);

}
