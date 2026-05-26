package efub.assignment.community.post.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.domain.PostLike;
import efub.assignment.community.post.dto.request.CreatePostRequestDto;
import efub.assignment.community.post.dto.request.UpdatePostRequestDto;
import efub.assignment.community.post.dto.response.PostListResponseDto;
import efub.assignment.community.post.dto.response.PostResponseDto;
import efub.assignment.community.post.dto.summary.PostSummaryDto;
import efub.assignment.community.post.repository.PostLikeRepository;
import efub.assignment.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;

    // 게시글 생성
    public PostResponseDto createPost(Long boardId, Long memberId, CreatePostRequestDto requestDto) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = Post.builder()
                .board(board)
                .member(member)
                .title(requestDto.title())
                .anonymous(requestDto.anonymous())
                .content(requestDto.content())
                .build();

        Post savedPost = postRepository.save(post);
        return PostResponseDto.from(savedPost, postLikeRepository.countByPost(post));
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public PostListResponseDto getPostsByBoard(Long boardId) {

        boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        List<PostSummaryDto> posts = postRepository
                .findAllByBoard_BoardIdOrderByCreatedAtDesc(boardId)
                .stream()
                .map(post -> PostSummaryDto.from(
                        post,
                        postLikeRepository.countByPost(post)
                ))
                .toList();

        Long totalPosts = postRepository.countByBoard_BoardId(boardId);

        return new PostListResponseDto(posts, totalPosts);
    }

    // 게시글 상세 조회
    public PostResponseDto getPost(Long postId) {
        // 존재 여부 먼저 확인
        Post post = findByPostId(postId);
        // 조회수 증가
        postRepository.increaseViewCount(postId);
        // 최신 조회수 반영
        post = findByPostId(postId);
        return PostResponseDto.from(post, postLikeRepository.countByPost(post));
    }

    // 게시글 수정
    public PostResponseDto updatePost(Long postId, Long memberId, UpdatePostRequestDto requestDto) {
        Post post = findByPostId(postId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        authorizePostWriter(post, member);
        post.updatePost(requestDto.title(), requestDto.content());
        return PostResponseDto.from(post, postLikeRepository.countByPost(post));
    }

    // 게시글 삭제
    public void deletePost(Long postId, Long memberId) {
        Post post = findByPostId(postId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        authorizePostWriter(post, member);
        postRepository.delete(post);
    }

    // 게시글 좋아요 생성
    public void likePost(Long postId, Long memberId) {
        Post post = findByPostId(postId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (postLikeRepository.existsByPostAndMember(post, member)) {
            throw new CustomException(ErrorCode.POST_LIKE_ALREADY_EXISTS);
        }

        PostLike postLike = PostLike.builder()
                .post(post)
                .member(member)
                .build();

        postLikeRepository.save(postLike);
    }

    // 게시글 좋아요 삭제
    public void unlikePost(Long postId, Long memberId) {
        Post post = findByPostId(postId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        PostLike postLike = postLikeRepository.findByPostAndMember(post, member)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_LIKE_NOT_FOUND));

        postLikeRepository.delete(postLike);
    }

    // helpers
    public Post findByPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private void authorizePostWriter(Post post, Member member) {
        if (!post.getMember().getMemberId().equals(member.getMemberId())) {
            throw new CustomException(ErrorCode.POST_MEMBER_MISMATCH);
        }
    }
}