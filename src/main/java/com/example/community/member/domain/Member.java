package com.example.community.member.domain;

import com.example.community.global.domain.BaseEntity;
import com.example.community.member.dto.request.UpdateMemberRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 255)
    private String university;

    @Column( length = 20)
    private String studentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status = MemberStatus.REGISTER;


    @Builder
    public Member(String email, String password, String nickname, String university, String studentId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.university = university;
        this.studentId = studentId;
        this.status = MemberStatus.REGISTER;
    }

    public void updateProfile(UpdateMemberRequestDto dto) {
        if (dto.email() != null)      this.email = dto.email();
        if (dto.nickname() != null)   this.nickname = dto.nickname();
    }

    public void changeStatus(MemberStatus status) {
        this.status = status;
    }
}
