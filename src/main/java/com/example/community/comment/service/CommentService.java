package com.example.community.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.community.comment.domain.Comment;
import com.example.community.comment.dto.request.CreateCommentRequest;
import com.example.community.comment.dto.request.UpdateCommentRequest;
import com.example.community.comment.dto.response.CommentListResponse;
import com.example.community.comment.dto.response.CommentResponse;
import com.example.community.comment.repository.CommentRepository;
import com.example.community.global.exception.CustomException;
import com.example.community.global.exception.ErrorCode;
import com.example.community.member.domain.Member;
import com.example.community.member.service.MemberService;
import com.example.community.notification.service.NotificationService;
import com.example.community.post.domain.Post;
import com.example.community.post.service.PostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final MemberService memberService;
    private final NotificationService notificationService;

    @Transactional
    public CommentResponse createComment(Long postId, Long memberId, CreateCommentRequest request) {
        Post post = postService.findByPostId(postId);
        Member writer = memberService.findByMemberId(memberId);

        Comment comment = Comment.builder()
                .post(post)
                .writer(writer)
                .content(request.content())
                .isAnonymous(request.isAnonymous())
                .build();

        Comment saved = commentRepository.save(comment);

        Member postWriter = post.getWriter();
        if (!postWriter.getMemberId().equals(writer.getMemberId())) {
            notificationService.createCommentNotification(
                    postWriter,
                    post.getBoard().getName(),
                    saved.getContent()
            );
        }

        return CommentResponse.from(saved);
    }

    @Transactional
    public void updateComment(Long commentId, Long memberId, UpdateCommentRequest request) {
        Comment comment = findByCommentId(commentId);
        Member member = memberService.findByMemberId(memberId);

        authorizeCommentWriter(comment, member);
        comment.changeContent(request.content());
    }

    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = findByCommentId(commentId);
        Member member = memberService.findByMemberId(memberId);

        authorizeCommentWriter(comment, member);
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public CommentListResponse getCommentsByPost(Long postId) {
        postService.findByPostId(postId);

        List<CommentResponse> comments = commentRepository.findAllByPostPostIdOrderByCreatedAtDesc(postId).stream()
                .map(CommentResponse::from)
                .toList();

        return CommentListResponse.of(comments);
    }

    @Transactional(readOnly = true)
    public CommentListResponse getCommentsByMember(Long memberId) {
        memberService.findByMemberId(memberId);

        List<CommentResponse> comments = commentRepository.findAllByWriterMemberIdOrderByCreatedAtDesc(memberId).stream()
                .map(CommentResponse::from)
                .toList();

        return CommentListResponse.of(comments);
    }

    private Comment findByCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void authorizeCommentWriter(Comment comment, Member member) {
        if (!comment.getWriter().getMemberId().equals(member.getMemberId())) {
            throw new CustomException(ErrorCode.COMMENT_ACCOUNT_MISMATCH);
        }
    }
}
