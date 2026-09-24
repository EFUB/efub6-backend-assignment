package efub.assignment.community.post.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostTest {

    private Post post;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .board(null)
                .writer(null)
                .title("제목")
                .content("본문")
                .isAnonymous(false)
                .build();
    }

    @Test
    void increaseLikeCount_호출_시_좋아요_수가_1_증가한다() {
        // when
        post.increaseLikeCount();

        // then
        assertEquals(1L, post.getLikeCount());
    }

    @Test
    void decreaseLikeCount_좋아요_수가_0보다_클_때_1_감소한다() {
        // given
        post.increaseLikeCount(); // likeCount = 1

        // when
        post.decreaseLikeCount();

        // then
        assertEquals(0L, post.getLikeCount());
    }

    @Test
    void decreaseLikeCount_좋아요_수가_0일_때_호출해도_0을_유지한다() {
        // given: likeCount 초기값 0

        // when
        post.decreaseLikeCount();

        // then
        assertEquals(0L, post.getLikeCount());
    }
}