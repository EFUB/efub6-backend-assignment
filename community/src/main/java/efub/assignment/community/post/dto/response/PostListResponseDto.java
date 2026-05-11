package efub.assignment.community.post.dto.response;

import efub.assignment.community.post.dto.summary.PostSummaryDto;

import java.util.List;

public record PostListResponseDto(
        List<PostSummaryDto> posts,
        Long totalPosts
) {
}