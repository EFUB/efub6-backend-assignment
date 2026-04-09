package efub.assignment.community.member.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    // 회원 이메일
    @Column(unique = true)
    private String email;

    // 회원 비밀번호
    @Column(nullable = false, updatable = false)
    private String password;

    // 회원 닉네임
    @Column(nullable = false)
    private String nickname;

    // 회원 대학
    @Column(nullable = false, updatable = false)
    private String university;

    // 회원 학번
    @Column(nullable = false, updatable = false)
    private String studentId;

    @Builder
    public Member(String email, String password, String nickname, String university, String studentId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.university = university;
        this.studentId = studentId;
    }

    // 프로필 업데이트: 닉네임 수정
    public void updateProfile(String nickname) {
        this.nickname = nickname;
    }
}
