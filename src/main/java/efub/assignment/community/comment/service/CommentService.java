package efub.assignment.community.comment.service;

import efub.assignment.community.comment.domain.Comment;
import efub.assignment.community.comment.dto.request.CreateCommentRequest;
import efub.assignment.community.comment.dto.request.UpdateCommentRequest;
import efub.assignment.community.comment.dto.response.CommentListResponse;
import efub.assignment.community.comment.dto.response.CommentResponse;
import efub.assignment.community.comment.repository.CommentRepository;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.notification.domain.Notification;
import efub.assignment.community.notification.domain.NotificationType;
import efub.assignment.community.notification.repository.NotificationRepository;
import efub.assignment.community.notification.service.NotificationService;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    @Transactional
    public Long createComment(Long postId, Long memberId, CreateCommentRequest request) {
        Member writerMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment newComment = request.toEntity(post, writerMember);
        commentRepository.save(newComment);

        // 내 글에 내가 댓글을 단게 아닐 때만 알림 생성
        if (!post.getWriter().getMemberId().equals(memberId)) {
            String boardName = post.getBoard().getName();

            notificationService.createCommentNotification(post.getWriter(), boardName, newComment.getContent());
        }

        return newComment.getId();
    }

    @Transactional(readOnly = true)
    public CommentListResponse getAllCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<Comment> comments = commentRepository.findAllByPost(post);

        List<CommentResponse> commentResponses = comments
                .stream()
                .map(CommentResponse::from)
                .toList();

        return  new CommentListResponse(commentResponses, (long) comments.size());
    }

    @Transactional(readOnly = true)
    public CommentListResponse getAllCommentsByWriter(Long memberId) {
        Member writer = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        List<Comment> comments = commentRepository.findAllByWriter(writer);

        List<CommentResponse> commentResponses = comments
                .stream()
                .map(CommentResponse::from)
                .toList();

        return new CommentListResponse(commentResponses, (long) comments.size());
    }

    @Transactional(readOnly = true)
    public CommentResponse getComment(Long commentId) {
        Comment comment = findByCommentId(commentId);
        return CommentResponse.from(comment);
    }

    @Transactional
    public void updateCommentContent(Long commentId, UpdateCommentRequest request, Long memberId) {
        Comment comment = findByCommentId(commentId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        authorizeCommentWriter(comment, member);
        comment.changeContent(request.content());
    }

    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = findByCommentId(commentId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        authorizeCommentWriter(comment, member);
        commentRepository.delete(comment);
    }

    private Comment findByCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void authorizeCommentWriter(Comment comment, Member member) {
        if(!comment.getWriter().getMemberId().equals(member.getMemberId())) {
            throw new CustomException(ErrorCode.COMMENT_ACCOUNT_MISMATCH);
        }
    }
}
