package efub.assignment.community.post.repository;

import efub.assignment.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 게시글 전체 조회 -> boardId에 해당되는 게시판의 게시물만 불러오도록 수정 필요
    List<Post> findAllByOrderByCreatedAtDesc();
}
