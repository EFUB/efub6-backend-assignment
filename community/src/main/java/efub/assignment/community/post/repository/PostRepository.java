package efub.assignment.community.post.repository;

import efub.assignment.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 게시판별 최신순 조회
    List<Post> findAllByBoard_BoardIdOrderByCreatedAtDesc(Long boardId);

    // 게시글 개수 (for totalPosts)
    Long countByBoard_BoardId(Long boardId);

    // 조회수 증가
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.postId = :postId")
    void increaseViewCount(@Param("postId") Long postId);
}