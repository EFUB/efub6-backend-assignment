package efub.assignment.community.comment.repository;

import efub.assignment.community.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPost_PostIdOrderByCreatedAt(Long postId);

    List<Comment> findAllByWriter_MemberIdOrderByCreatedAtDesc(Long memberId);
}