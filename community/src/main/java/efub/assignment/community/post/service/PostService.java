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

    @Transactional
    public PostResponse createPost(Long boardId, Long memberId, @Valid PostCreateRequest request) {
        Member author = memberService.findByMemberId(memberId);
        Board board = boardService.findByBoardId(boardId);

        Post post = request.toEntity(author, board);
        Post savedPost = postRepository.save(post);

        return PostResponse.from(savedPost);
    }

    //게시판의 게시글 목록 조회
    @Transactional(readOnly = true)
    public PostListResponse getAllPosts(Long boardId) {
        boardService.findByBoardId(boardId);

        List<PostSummary> postSummaries = postRepository.findAllByBoard_BoardIdOrderByCreatedAtDesc(boardId)
                .stream()
                .map(PostSummary::from)
                .toList();
        return new PostListResponse(postSummaries);

    }

    //게시글 단 건 조회
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = findByPostId(postId);
        return PostResponse.from(post);
    }

    @Transactional
    public PostResponse updatePost(Long postId, Long memberId, @Valid PostUpdateRequest request) {
        Post post = findByPostId(postId);
        Member member = memberService.findByMemberId(memberId);

        authorizePostAuthor(post, member);
        post.changeContent(request.content());

        return PostResponse.from(post);
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Post post = findByPostId(postId);
        Member member = memberService.findByMemberId(memberId);

        authorizePostAuthor(post, member);
        postRepository.delete(post);
    }

    public Post findByPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private void authorizePostAuthor(Post post, Member member) {
        if (!post.getAuthor().equals(member)) {
            throw new CustomException(ErrorCode.POST_MEMBER_MISMATCH);
        }
    }
}