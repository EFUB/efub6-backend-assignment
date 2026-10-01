package com.example.community.post.service;

import com.example.community.board.service.BoardService;
import com.example.community.global.exception.CustomException;
import com.example.community.global.exception.ErrorCode;
import com.example.community.member.domain.Member;
import com.example.community.member.service.MemberService;
import com.example.community.post.domain.Post;
import com.example.community.post.dto.request.UpdatePostRequest;
import com.example.community.post.repository.PostLikeRepository;
import com.example.community.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private MemberService memberService;
    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private BoardService boardService;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("작성자 본인이 수정 요청하면 제목 및 내용이 바뀐다")
    void update_post_content_by_writer() {
        // given
        UpdatePostRequest request = new UpdatePostRequest("홍길동", "안녕");

        Member writer = mock(Member.class);
        given(writer.getMemberId()).willReturn(1L);

        Post post = mock(Post.class);
        given(post.getWriter()).willReturn(writer);

        given(postRepository.findById(100L)).willReturn(Optional.of(post));
        given(memberService.findByMemberId(1L)).willReturn(writer);

        // when
        postService.updatePostContent(100L, 1L, request);

        // then
        verify(post).changePost(request.title(), request.content());
    }

    @Test
    @DisplayName("작성자가 아닌 회원이 수정 요청하면 예외 발생하고 게시글은 변경되지 않는다")
    void update_post_content_by_non_writer() {
        // given
        Long postId = 100L;
        Long writerId = 1L;
        Long otherMemberId = 2L;

        UpdatePostRequest request = new UpdatePostRequest("홍길동", "안녕");

        Member writer = mock(Member.class);
        given(writer.getMemberId()).willReturn(writerId);

        Member otherMember = mock(Member.class);
        given(otherMember.getMemberId()).willReturn(otherMemberId);

        Post post = mock(Post.class);
        given(post.getWriter()).willReturn(writer);

        given(postRepository.findById(100L)).willReturn(Optional.of(post));
        given(memberService.findByMemberId(2L)).willReturn(otherMember);

        // when & then
        assertThrows(CustomException.class, () -> postService.updatePostContent(postId, otherMemberId, request));
        verify(post, never()).changePost(any(), any());
    }
}