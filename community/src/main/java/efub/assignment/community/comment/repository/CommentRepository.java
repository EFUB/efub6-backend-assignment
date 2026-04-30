package efub.assignment.community.comment.repository;

import efub.assignment.community.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시물 댓글 조회
    List<Comment> findAllByPostIdOrderByCreatedAtDesc(Long postId);

    // 특정 사용자 작성 댓글 조회
    List<Comment> findAllByWriterMemberIdOrderByCreatedAtDesc(Long memberId);
}
