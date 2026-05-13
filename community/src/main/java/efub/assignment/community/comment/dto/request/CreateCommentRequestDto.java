package efub.assignment.community.comment.dto.request;

import efub.assignment.community.comment.domain.Comment;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommentRequestDto(
        @NotNull(message = "멤버 id는 필수입니다.")
        Long memberId,

        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(max = 1000, message = "댓글은 1000자 이하로 입력해야 합니다.")
        String content
) {
        public Comment toEntity(Member member, Post post) {
                return Comment.builder()
                        .writer(member)
                        .post(post)
                        .content(content)
                        .build();
        }
}