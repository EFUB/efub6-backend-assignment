package efub.assignment.community.post.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.service.BoardService;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.domain.PostLike;
import efub.assignment.community.post.dto.request.PostCreateRequestDto;
import efub.assignment.community.post.dto.request.PostUpdateRequestDto;
import efub.assignment.community.post.dto.response.PostListResponseDto;
import efub.assignment.community.post.dto.response.PostResponseDto;
import efub.assignment.community.post.repositoriy.PostLikeRepository;
import efub.assignment.community.post.repositoriy.PostRepository;
import efub.assignment.community.post.summary.PostSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final MemberService memberService;
    private final BoardService boardService;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public Long createPost(PostCreateRequestDto request) {
        Board board = boardService.findByBoardId(request.getBoardId());
        Member writer = memberService.findByMemberId(request.getMemberId());

        Post newPost = request.toEntity(board, writer);
        postRepository.save(newPost);

        return newPost.getPostId();
    }

    @Transactional
    public PostResponseDto getPost(Long postId) {
        Post post = findByPostId(postId);
        postRepository.increaseViewCount(postId);
        post = findByPostId(postId);

        return PostResponseDto.from(post);
    }

    @Transactional
    public PostListResponseDto getAllPosts() {
        List<PostSummary> postSummaries = postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PostSummary::from)
                .toList();

        return new PostListResponseDto(postSummaries, postRepository.count());
    }

    @Transactional
    public void updatePostContent(Long postId, Long memberId, PostUpdateRequestDto request) {
        Post post = findByPostId(postId);
        Member writer = memberService.findByMemberId(memberId);

        authorizePostWriter(post,writer);
        post.changeContent(request.getContent());
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Post post = findByPostId(postId);
        Member member = memberService.findByMemberId(memberId);

        authorizePostWriter(post, member);
        postRepository.delete(post);
    }

    @Transactional
    public void likePost (Long postId, Long memberId) {
        Post post = findByPostId(postId);
        Member member = memberService.findByMemberId(memberId);

        if(postLikeRepository.existsByPostAndMember(post, member)) {
            throw new CustomException(ErrorCode.POST_LIKE_ALREADY_EXISTS);
        }

        PostLike like = PostLike.builder()
                .post(post)
                .member(member)
                .build();

        postLikeRepository.save(like);
    }

    @Transactional
    public void unlikePost (Long postId, Long memberId) {
        Post post = findByPostId(postId);
        Member member = memberService.findByMemberId(memberId);

        PostLike like = postLikeRepository.findByPostAndMember(post, member)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_LIKE_NOT_FOUND));
        postLikeRepository.delete(like);
    }

    public Post findByPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    public void authorizePostWriter(Post post, Member member) {
        if(!post.getWriter().equals(member)) {
            throw new CustomException(ErrorCode.POST_ACCOUNT_MISMATCH);
        }
    }

}





