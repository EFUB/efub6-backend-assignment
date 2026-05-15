package efub.assignment.community.comment.service;

import efub.assignment.community.comment.domain.Comment;
import efub.assignment.community.comment.dto.request.CommentRequest;
import efub.assignment.community.comment.dto.response.CommentResponse;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.dto.response.MemberCommentResponse;
import efub.assignment.community.post.dto.response.PostCommentResponse;
import efub.assignment.community.comment.repository.CommentRepository;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentRepository commentRepository;

    public CommentResponse createComment(Long postId, Long memberId, CommentRequest request) {
        Member writer = memberService.findByMemberId(memberId);
        Post post = postService.findByPostId(postId);

        Comment comment = request.toEntity(writer, post);
        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.of(savedComment);
    }

    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        Comment comment = findByCommentId(commentId);

        comment.updateComment(request.getContent(), request.getIsAnonymous());

        return CommentResponse.of(comment);
    }

    @Transactional(readOnly = true)
    public PostCommentResponse getPostCommentList(Long postId) {
        postService.findByPostId(postId);

        List<Comment> commentList = commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId);

        return PostCommentResponse.of(postId, commentList);
    }

    @Transactional(readOnly = true)
    public MemberCommentResponse getMemberCommentList(Long memberId) {
        Member member = memberService.findByMemberId(memberId);

        List<Comment> commentList = commentRepository.findAllByWriterMemberIdOrderByCreatedAtDesc(memberId);

        return MemberCommentResponse.of(member, commentList);
    }


    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = findByCommentId(commentId);
        Member member = memberService.findByMemberId(memberId);
        authorizeCommentWriter(comment, member);
        commentRepository.delete(comment);
    }

    private Comment findByCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void authorizeCommentWriter(Comment comment,Member member){
        if(!comment.getWriter().equals(member)){
            throw new CustomException(ErrorCode.COMMENT_ACCOUNT_MISMATCH);
        }
    }
}

