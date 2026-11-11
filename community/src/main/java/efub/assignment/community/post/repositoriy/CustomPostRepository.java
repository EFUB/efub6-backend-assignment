package efub.assignment.community.post.repositoriy;


import efub.assignment.community.post.domain.Post;

import java.util.List;

public interface CustomPostRepository {
    List<Post> search(String keyword, String writerName);
}
