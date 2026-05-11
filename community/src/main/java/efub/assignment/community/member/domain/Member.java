package efub.assignment.community.member.domain;

import efub.assignment.community.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name="members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false,updatable = false)
    private String studentId;

    @Column(nullable = false)
    private String university;

    @Column(nullable = false)
    private String nickname;

    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberStatus status = MemberStatus.REGISTER;


    @Builder
    public Member(String studentId,String university,String nickname,String email,String password){
        this.studentId = studentId;
        this.university = university;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }
    public void changeStatus(MemberStatus status){
        this.status = status;
    }

    //회원정보 수정
    public void updateNickname(String nickname){
        this.nickname = nickname;
    }
    public void updateEmail(String email){
        this.email = email;
    }
}
