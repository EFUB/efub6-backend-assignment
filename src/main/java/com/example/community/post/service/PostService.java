package com.example.community.post.service;

import com.example.community.board.domain.Board;
import com.example.community.board.service.BoardService;
import com.example.community.global.exception.CustomException;
import com.example.community.global.exception.ErrorCode;
import com.example.community.member.domain.Member;
import com.example.community.member.service.MemberService;
import com.example.community.post.domain.Post;
import com.example.community.post.domain.PostLike;
import com.example.community.post.dto.request.CreatePostRequest;
import com.example.community.post.dto.request.UpdatePostRequest;
import com.example.community.post.dto.response.PostListResponse;
import com.example.community.post.dto.response.PostResponse;
import com.example.community.post.dto.summary.PostSummary;
import com.example.community.post.repository.PostLikeRepository;
import com.example.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final MemberService memberService;
    private final BoardService boardService;

    @Transactional
    public PostResponse createPost(Long boardId, Long memberId, CreatePostRequest request) {
        Member member = memberService.findByMemberId(memberId);
        Board board = boardService.findByBoardId(boardId);

        Post post = request.toEntity(member, board);
        Post saved = postRepository.save(post);

        return PostResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PostListResponse getAllPosts(Long boardId) {
        List<PostSummary> postSummaries = postRepository
                .findAllByBoardBoardIdOrderByCreatedAtDesc(boardId)
                .stream()
                .map(PostSummary::from)
                .toList();

        return PostListResponse.of(postSummaries);
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = findByPostId(postId);
        return PostResponse.from(post);
    }

    @Transactional
    public void updatePostContent(Long postId, Long memberId, UpdatePostRequest request) {
        Post post = findByPostId(postId);
        Member member = memberService.findByMemberId(memberId);

        authorizePostWriter(post, member);
        post.changePost(request.title(), request.content());
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Post post = findByPostId(postId);
        Member member = memberService.findByMemberId(memberId);

        authorizePostWriter(post, member);
        postRepository.delete(post);
    }

    @Transactional
    public void createPostLike(Long postId, Long memberId) {
        Post post = findByPostId(postId);
        Member member = memberService.findByMemberId(memberId);

        if (postLikeRepository.existsByPostAndMember(post, member)) {
            throw new CustomException(ErrorCode.POST_LIKE_ALREADY_EXISTS);
        }

        PostLike postLike = PostLike.builder()
                .post(post)
                .member(member)
                .build();
        postLikeRepository.save(postLike);
    }

    @Transactional
    public void deletePostLike(Long postId, Long memberId) {
        Post post = findByPostId(postId);
        Member member = memberService.findByMemberId(memberId);

        PostLike postLike = postLikeRepository.findByPostAndMember(post, member)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_LIKE_NOT_FOUND));

        postLikeRepository.delete(postLike);
    }

    @Transactional(readOnly = true)
    public Post findByPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private void authorizePostWriter(Post post, Member member) {
        if (!Objects.equals(post.getWriter().getMemberId(), member.getMemberId())) {
            throw new CustomException(ErrorCode.POST_ACCOUNT_MISMATCH);
        }
    }
}
