package efub.assignment.community.post.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.domain.PostLike;
import efub.assignment.community.post.dto.request.CreatePostRequest;
import efub.assignment.community.post.dto.request.UpdatePostRequest;
import efub.assignment.community.post.dto.response.PostListResponse;
import efub.assignment.community.post.dto.response.PostResponse;
import efub.assignment.community.post.dto.summary.PostSummary;
import efub.assignment.community.post.repository.PostLikeRepository;
import efub.assignment.community.post.repository.PostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public Long createPost(Long boardId, Long memberId, @Valid CreatePostRequest request) {
        Member member = findByMemberId(memberId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        Post newPost = request.toEntity(board, member);

        postRepository.save(newPost);
        return newPost.getId();
    }

    @Transactional(readOnly = true)
    public PostListResponse getAllPosts() {
        List<PostSummary> postSummaries = postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PostSummary::from).toList();
        return new PostListResponse(postSummaries, postRepository.count());
    }

    @Transactional
    public PostResponse getPost(Long postId) {
        // 조회수 증가
        postRepository.increaseViewCount(postId);

        Post post = findByPostId(postId);
        return PostResponse.from(post);
    }

    @Transactional
    public void updatePostContent(Long postId, UpdatePostRequest request, Long memberId) {
        Post post = findByPostId(postId);
        Member member = findByMemberId(memberId);

        authorizePostWriter(post, member);
        post.changeContent(request.content());
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Post post = findByPostId(postId);
        Member member = findByMemberId(memberId);

        authorizePostWriter(post, member);
        postRepository.delete(post);
    }

    @Transactional
    public PostResponse likePost(Long postId, Long memberId) {
        Post post = findByPostId(postId);
        Member member = findByMemberId(memberId);
        if(postLikeRepository.existsByPostAndMember(post, member)) {
            throw new CustomException(ErrorCode.POST_LIKE_ALREADY_EXISTS);
        }
        PostLike postLike = PostLike.builder()
                .post(post)
                .member(member)
                .build();
        postLikeRepository.save(postLike);
        post.increaseLikeCount();
        return PostResponse.from(post);
    }

    @Transactional
    public PostResponse unlikePost(Long postId, Long memberId) {
        Post post = findByPostId(postId);
        Member member = findByMemberId(memberId);
        PostLike postLike = postLikeRepository.findByPostAndMember(post, member)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_LIKE_NOT_FOUND));
        postLikeRepository.delete(postLike);
        post.decreaseLikeCount();
        return PostResponse.from(post);
    }

    private Post findByPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private void authorizePostWriter(Post post, Member member) {
        // 객체 자체가 아니라, 고유 식별자인 memberId(PK)를 비교
        if (!post.getWriter().getMemberId().equals(member.getMemberId())) {
            throw new CustomException(ErrorCode.POST_ACCOUNT_MISMATCH);
        }
    }

}
