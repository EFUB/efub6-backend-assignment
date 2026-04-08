package efub.assignment.community.post.domain;

import efub.assignment.community.global.domain.BaseEntity;
import efub.assignment.community.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 게시글을 조회할 때 Member 정보까지 무조건 다 끌어 오면 DB가 아파함
    // Member 정보는 내가 진짜 사용할 때만 가져오는 지연 로딩 -> 최적화 옵션
    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isAnonymous;

    @Column(nullable = false)
    private Long viewCount;

    // 처음 default value가 있을 경우 파라미터롤 받지 않음!
    @Builder
    public Post(Member writer, String title, String content, boolean isAnonymous) {
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.viewCount = 0L;
    }

    public void changeContent(String newContent) {
        this.content = newContent;
    }
}
