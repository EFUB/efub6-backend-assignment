package efub.assignment.community.post.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MembersService;
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
    private final PostRepository postRepository;
    private final MembersService membersService;
    private final BoardRepository boardRepository;

    @Transactional
    public Long createPost(Long boardId, Long memberId, @Valid PostCreateRequest request) {
        Member writerMember = membersService.findByMemberId(memberId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        Post newPost = request.toEntity(board, writerMember);

        postRepository.save(newPost);
        return newPost.getId();
    }
    
    @Transactional
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
    public void updatePostContent(Long postId, PostUpdateRequest request, Long memberId) {
        Post post = findByPostId(postId);
        Member member = membersService.findByMemberId(memberId);

        authorizePostWriter(post, member);
        post.changeContent(request.content());
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Post post = findByPostId(postId);
        Member member = membersService.findByMemberId(memberId);

        authorizePostWriter(post, member);
        postRepository.delete(post);
    }

    public Post findByPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private void authorizePostWriter(Post post, Member member) {
        // 객체 자체가 아니라, 고유 식별자인 memberId(PK)를 비교
        if(!post.getWriter().getMemberId().equals(member.getMemberId())) {
            throw new CustomException(ErrorCode.POST_ACCOUNT_MISMATCH);
        }
    }

}
