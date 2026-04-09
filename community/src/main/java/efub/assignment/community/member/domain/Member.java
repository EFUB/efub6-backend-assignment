package efub.assignment.community.member.domain;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name= "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long memberId;

    //회원 학번(중복불가)
    @Column(unique = true, nullable = false, updatable = false)
    private Long studentId;

    //회원 학교
    @Column(nullable = false, updatable = false)
    private String university;

    //회원 닉네임
    @Column(nullable = false)
    private String nickname;

    //회원 이메일
    @Column(unique = true, nullable = false)
    private String email;

    //회원 비밀번호
    @Column(nullable = false)
    private String password;

    //생성일
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //수정일
    @Column
    private LocalDateTime updatedAt;

    @Builder
    public Member(Long studentId, String university, String nickname, String email, String password) {
        this.studentId = studentId;
        this.university = university;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    //생성일, 수정일 자동으로 들어가도록 함
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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
