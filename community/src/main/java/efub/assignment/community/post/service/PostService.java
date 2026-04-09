package efub.assignment.community.post.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.service.BoardService;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostCreateRequest;
import efub.assignment.community.post.dto.request.PostUpdateRequest;
import efub.assignment.community.post.dto.response.PostListResponse;
import efub.assignment.community.post.dto.response.PostResponse;
import efub.assignment.community.post.dto.summary.PostSummary;
import efub.assignment.community.post.repository.PostRepository;
import jakarta.validation.Valid;
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

    // 게시글 생성
    @Transactional
    public PostResponse createPost(Long boardId, Long memberId, PostCreateRequest request) {
        Member writerAccount = memberService.findByMemberId(memberId);
        Board board = boardService.findByBoardId(boardId);

        Post newPost = request.toEntity(board, writerAccount);
        postRepository.save(newPost);
        return PostResponse.from(newPost);
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public PostListResponse getAllPosts() {
        List<PostSummary> postSummaries = postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PostSummary::from)
                .toList();
        return new PostListResponse(postSummaries, postRepository.count());
    }

    // 게시글 상세 조회
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = findByPostId(postId);
        return PostResponse.from(post);
    }

    // 게시글 수정
    @Transactional
    public PostResponse updatePostContent(Long postId, Long memberId, @Valid PostUpdateRequest request) {
        Member member = memberService.findByMemberId(memberId);
        Post post = findByPostId(postId);

        // 게시글 작성자 = 계정 사용자
        authorizePostWriter(post, member);
        post.changePostContent(request.content());

        return PostResponse.from(post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Member member = memberService.findByMemberId(memberId);
        Post post = findByPostId(postId);

        // 게시굴 작성자 = 계정 사용자
        authorizePostWriter(post, member);
        postRepository.delete(post);
    }

    private void authorizePostWriter(Post post, Member member) {
        if (!post.getWriter().equals(member)) {
            throw new CustomException(ErrorCode.POST_ACCOUNT_MISMATCH);
        }
    }

    private Post findByPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}
