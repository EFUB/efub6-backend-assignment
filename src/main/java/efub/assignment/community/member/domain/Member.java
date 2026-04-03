package efub.assignment.community.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    // 멤버 이메일
    @Column(nullable = false, unique = true)
    private String email;

    // 멤버 비밀번호
    @Column(nullable = false)
    private String password;

    // 멤버 닉네임
    @Column(nullable = false)
    private String nickname;

    // 멤버 학교
    @Column(nullable = false)
    private String school;

    // 멤버 학번
    @Column(nullable = false)
    private String studentId;

    // 멤버 상태
    @Enumerated(EnumType.STRING)
    private MemberStatus status = MemberStatus.REGISTER;

    // 멤버 생성일자
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    // 멤버 수정일자
    @LastModifiedDate
    @Column(nullable = true)
    private LocalDateTime modifiedAt;

    @Builder
    public Member(String email, String password, String nickname, String school, String studentId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.school = school;
        this.studentId = studentId;
        this.createdAt = LocalDateTime.now();
    }

    public void changeStatus(MemberStatus status) {
        this.status = status;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
