package efub.assignment.community.comment.service;

import efub.assignment.community.comment.domain.Comment;
import efub.assignment.community.comment.dto.request.CreateCommentRequestDto;
import efub.assignment.community.comment.dto.request.UpdateCommentRequestDto;
import efub.assignment.community.comment.dto.response.CommentListResponseDto;
import efub.assignment.community.comment.dto.response.CommentResponseDto;
import efub.assignment.community.comment.repository.CommentRepository;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import efub.assignment.community.notification.service.NotificationService;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

    // 댓글 생성
    public CommentResponseDto createComment(Long postId, CreateCommentRequestDto requestDto) {
        Member writer = memberService.findByMemberId(requestDto.memberId());
        Post post = postService.findByPostId(postId);

        Comment comment = requestDto.toEntity(writer, post);
        Comment savedComment = commentRepository.save(comment);

        // 알림 생성
        notificationService.createCommentNotification(
                post.getMember(),
                post.getBoard().getBoardName(),
                savedComment.getContent()
        );

        return CommentResponseDto.from(savedComment);
    }

    // 댓글 1개 조회
    @Transactional(readOnly = true)
    public CommentResponseDto getComment(Long commentId) {
        Comment comment = findByCommentId(commentId);
        return CommentResponseDto.from(comment);
    }

    // 게시글별 댓글 목록 조회
    @Transactional(readOnly = true)
    public CommentListResponseDto getCommentsByPost(Long postId) {
        postService.findByPostId(postId);

        return CommentListResponseDto.from(
                commentRepository.findAllByPost_PostIdOrderByCreatedAt(postId)
        );
    }

    // 회원별 댓글 목록 조회
    @Transactional(readOnly = true)
    public CommentListResponseDto getCommentsByMember(Long memberId) {
        memberService.findByMemberId(memberId);

        return CommentListResponseDto.from(
                commentRepository.findAllByWriter_MemberIdOrderByCreatedAtDesc(memberId)
        );
    }

    // 댓글 수정
    public CommentResponseDto updateComment(Long commentId, Long memberId, UpdateCommentRequestDto requestDto) {
        Comment comment = findByCommentId(commentId);
        authorizeCommentWriter(comment, memberId);

        comment.updateContent(requestDto.content());

        return CommentResponseDto.from(comment);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = findByCommentId(commentId);
        authorizeCommentWriter(comment, memberId);

        commentRepository.delete(comment);
    }

    // helper
    public Comment findByCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }
    private void authorizeCommentWriter(Comment comment, Long memberId) {
        if (!comment.getWriter().getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.COMMENT_MEMBER_MISMATCH);
        }
    }
}