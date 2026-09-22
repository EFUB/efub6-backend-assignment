package efub.assignment.community.member.domain;


import efub.assignment.community.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name= "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long memberId;

    //카카오 사용자 식별 ID
    @Column(unique=true)
    private Long kakaoId;

    //회원 학번(중복불가)
    @Column(unique = true, updatable = false)
    private Long studentId;

    //회원 학교
    @Column(updatable = false)
    private String university;

    //회원 닉네임
    @Column(nullable = false)
    private String nickname;

    //회원 이메일
    @Column(unique = true)
    private String email;

    //회원 비밀번호
    private String password;

    //카카오 프로필 이미지
    private String profileImage;



    @Builder
    public Member(Long kakaoId, Long studentId, String university, String nickname, String email, String password, String profileImage) {
        this.kakaoId = kakaoId;
        this.studentId = studentId;
        this.university = university;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }


    //수정: 닉네임(선택), 이메일(선택), 비밀번호(필수)
    public void updateMember(String nickname, String email, String password){
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (email != null) {
            this.email = email;
        }
        this.password = password;
    }


}
