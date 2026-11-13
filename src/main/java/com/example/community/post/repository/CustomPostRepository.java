package com.example.community.post.repository;

import com.example.community.post.domain.Post;
import com.example.community.post.dto.condition.PostSearchCondition;

import java.util.List;

public interface CustomPostRepository {
    List<Post> search(PostSearchCondition cond);
}
