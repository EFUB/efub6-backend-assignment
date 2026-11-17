package efub.assignment.community.post.repository;

import efub.assignment.community.post.domain.Post;

import java.util.List;

public interface CustomPostRepository {

    // 키워드(제목 or 내용), 작성자 닉네임으로 게시글 동적 검색
    List<Post> search(String keyword, String writerNickname);
}
