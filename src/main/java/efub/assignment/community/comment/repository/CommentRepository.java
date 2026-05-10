package efub.assignment.community.comment.repository;

import efub.assignment.community.comment.domain.Comment;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 생성한 날짜 정렬 전체 조회
    List<Comment> findAllByOrderByCreatedAtDesc();

    // 게시글별 댓글 목록 조회
    List<Comment> findAllByPost(Post post);

    // 작성자별 댓글 목록 조회
    List<Comment> findAllByWriter(Member writer);
}
