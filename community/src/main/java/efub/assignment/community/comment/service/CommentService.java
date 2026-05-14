package efub.assignment.community.comment.service;

import efub.assignment.community.comment.domain.Comment;
import efub.assignment.community.comment.dto.request.CommentCreateRequest;
import efub.assignment.community.comment.dto.request.CommentUpdateRequest;
import efub.assignment.community.comment.repository.CommentRepository;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.dto.response.MemberCommentResponse;
import efub.assignment.community.member.service.MemberService;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.response.PostCommentResponse;
import efub.assignment.community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentRepository commentRepository;
    private ErrorCode ErrorCode;

    // 댓글 생성
    @Transactional
    public Long createComment(Long postId, CommentCreateRequest request, Long memberId) {
        Member writer = memberService.findByMemberId(memberId);
        Post post = postService.findByPostId(postId);

        Comment newComment = request.toEntity(writer, post);
        commentRepository.save(newComment);

        return newComment.getId();
    }

    // 특정 게시물 댓글 조회
    @Transactional(readOnly = true)
    public PostCommentResponse getPostCommentList(Long postId) {
        postService.findByPostId(postId);  // 게시글이 있는지 먼저 확인
        List<Comment> commentList = commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
        return PostCommentResponse.of(postId, commentList);
    }

    // 특정 사용자 댓글 조회
    @Transactional
    public MemberCommentResponse getMemberCommentList(Long memberId) {
        List<Comment> commentList = commentRepository.findAllByWriterMemberIdOrderByCreatedAtDesc(memberId);
        return MemberCommentResponse.of(memberId, commentList);
    }

    // 댓글 수정
    @Transactional
    public void updateCommentContent(Long commentId, CommentUpdateRequest request, Long memberId) {
        Comment comment = findByCommentId(commentId);
        Member member = memberService.findByMemberId(memberId);

        authorizeCommentWriter(comment, member);
        comment.changeContent(request.getContent());
    }

    private void authorizeCommentWriter(Comment comment, Member member) {
        if(!comment.getWriter().equals(member)) {
            throw new CustomException(ErrorCode.COMMENT_ACCOUNT_MISMATCH);
        }
    }

    private Comment findByCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
