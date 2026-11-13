package com.example.community.post.repository;

import com.example.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomPostRepository {

    List<Post> search(String keyword);
}
