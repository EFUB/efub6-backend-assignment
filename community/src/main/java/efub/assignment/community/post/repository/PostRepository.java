package efub.assignment.community.post.repository;

import efub.assignment.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 특정 게시판의 게시글을 생성일 기준 내림차순 조회
    List<Post> findAllByBoard_BoardIdOrderByCreatedAtDesc(Long boardId);
}