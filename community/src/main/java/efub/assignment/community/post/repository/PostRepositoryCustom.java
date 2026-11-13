package efub.assignment.community.post.repository;

import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostSearchCondition;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> searchByConditions(Long boardId, PostSearchCondition condition);

    List<Post> searchByBoardIdAndKeyword(Long boardId, String keyword);
}
