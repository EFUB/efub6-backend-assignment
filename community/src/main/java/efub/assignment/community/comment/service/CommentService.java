package efub.assignment.community.comment.service;

import efub.assignment.community.comment.domain.Comment;
import efub.assignment.community.comment.dto.request.CommentRequest;
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

    // [댓글 생성]
    @Transactional
    public Long createComment(Long postId, CommentRequest request) {
        Long memberId = request.getMemberId();
        Member writer = memberService.findByMemberId(memberId);
        Post post = postService.findByPostId(postId);

        Comment newComment = request.toEntity(writer, post);
        commentRepository.save(newComment);

        return newComment.getCommentId();
    }

    //[게시글별 댓글 조회]
    @Transactional(readOnly = true)
    public PostCommentResponse getPostCommentList(Long postId) {
        List<Comment> commentList = commentRepository.findAllByPostPostIdOrderByCreatedAt(postId);

        return PostCommentResponse.of(postId, commentList);
    }

    //[사용자ID별 댓글 조회]
    @Transactional (readOnly = true)
    public MemberCommentResponse getMemberCommentList(Long memberId) {
        Member member = memberService.findByMemberId(memberId);
        List<Comment> commentList = commentRepository.findAllByWriterMemberIdOrderByCreatedAtDesc(memberId);

        return MemberCommentResponse.of(member, commentList);
    }

    //[댓글 수정]
    @Transactional
    public void updateCommentContent(Long memberId, Long commentId, CommentUpdateRequest request) {
        Member member = memberService.findByMemberId(memberId);
        Comment comment = findByCommentId(commentId);

        authorizeCommentOwner(member, comment);
        comment.updateContent(request.getContent());
    }

    public void authorizeCommentOwner(Member member, Comment comment) {
        if (!comment.getWriter().equals(member)) {
            throw new CustomException(ErrorCode.COMMENT_ACCOUNT_MISMATCH);
        }
    }

    public Comment findByCommentId(Long commentId) {
        return commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
