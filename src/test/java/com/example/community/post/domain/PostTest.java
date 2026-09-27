package com.example.community.post.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    private Post post;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .title("제목")
                .content("내용")
                .isAnonymous(true)
                .build();
    }

    @Test
    @DisplayName("제목과 내용 모두 변경")
    void change_post_title_and_content() {
        // when
        post.changePost("홍길동", "안녕");

        // then
        assertEquals("홍길동", post.getTitle());
        assertEquals("안녕", post.getContent());
    }

    @Test
    @DisplayName("내용만 변경 - 제목에 null을 넘기면 기존 제목이 유지")
    void change_post_content() {
        // when
        post.changePost(null, "안녕");

        // then
        assertEquals("제목", post.getTitle());
        assertEquals("안녕", post.getContent());
    }

    @Test
    @DisplayName("제목만 변경 - 내용에 null을 넘기면 기존 내용이 유지")
    void change_post_title() {
        // when
        post.changePost("홍길동", null);

        // then
        assertEquals("홍길동", post.getTitle());
        assertEquals("내용", post.getContent());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    @DisplayName("제목이 공백(빈 문자열, 스페이스만)이면 수정 거부")
    void change_post_invalid_title(String invalidTitle) {

        // when & then
        assertThrows(IllegalArgumentException.class, () -> post.changePost(invalidTitle, null));
    }
}