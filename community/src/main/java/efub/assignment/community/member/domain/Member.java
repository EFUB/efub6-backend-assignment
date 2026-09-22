package efub.assignment.community.member.domain;

import efub.assignment.community.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import efub.assignment.community.comment.domain.Comment;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(
        name = "members",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_members_provider_provider_id",
                columnNames = {"provider", "provider_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AuthProvider provider;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Column(updatable = false)
    private String studentId;

    private String university;

    @Column(nullable = false)
    private String nickname;

    @Column(unique = true)
    private String email;

    @Column(length = 1000)
    private String profileImage;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberStatus status = MemberStatus.REGISTER;


    @Builder
    public Member(String studentId, String university, String nickname, String email, String password) {
        this.provider = AuthProvider.LOCAL;
        this.providerId = email;
        this.studentId = studentId;
        this.university = university;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    public static Member createKakao(String providerId, String email, String nickname) {
        return createKakao(providerId, email, nickname, null);
    }

    public static Member createKakao(
            String providerId,
            String email,
            String nickname,
            String profileImage
    ) {
        if (providerId == null || providerId.isBlank()) {
            throw new IllegalArgumentException("카카오 회원 식별자는 필수입니다.");
        }

        Member member = new Member();
        member.provider = AuthProvider.KAKAO;
        member.providerId = providerId;
        member.email = email;
        member.nickname = nickname == null || nickname.isBlank()
                ? "kakao_" + providerId
                : nickname;
        member.profileImage = profileImage;
        return member;
    }

    public void changeStatus(MemberStatus status) {
        this.status = status;
    }

    //회원정보 수정
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateEmail(String email) {
        this.email = email;
        if (provider == null || provider == AuthProvider.LOCAL) {
            this.provider = AuthProvider.LOCAL;
            this.providerId = email;
        }
    }

    public void updateOAuthProfile(String email, String nickname) {
        updateOAuthProfile(email, nickname, null);
    }

    public void updateOAuthProfile(String email, String nickname, String profileImage) {
        if (email != null && !email.isBlank()) {
            this.email = email;
        }
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
        if (profileImage != null && !profileImage.isBlank()) {
            this.profileImage = profileImage;
        }
    }
}
