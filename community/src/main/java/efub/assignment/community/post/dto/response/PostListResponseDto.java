package efub.assignment.community.post.dto.response;

import efub.assignment.community.post.summary.PostSummary;

import java.util.List;

public record PostListResponseDto (
    List<PostSummary> posts,
    Long totalPosts
) {}
