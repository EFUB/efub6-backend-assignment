package efub.assignment.community.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member")
//파라미터가 없는 기본 생성자를 자동으로 만들어줌
//기본 생성자의 접근권한을 protected로 제한.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//멤버 엔티티, 테이블과 매핑
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String email;

    @Column (nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String university;

    @Column(nullable = false, unique = true)
    private String studentId;

    @Enumerated (EnumType.STRING)
    private MemberStatus memberStatus;

    @Builder
    public Member(String email, String password, String nickname,
                  String university, String studentId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.university = university;
        this.studentId = studentId;
        this.memberStatus = MemberStatus.REGISTER;
    }

    public void updateProfile(String email, String password, String nickname,
                              String university, String studentId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.university = university;
        this.studentId = studentId;
    }

    public void changeStatus(MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
    }

}
