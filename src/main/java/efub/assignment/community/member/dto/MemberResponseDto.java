package efub.assignment.community.member.dto;

import efub.assignment.community.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder @Getter
public class MemberResponseDto {
    private Long id;
    private String nickname;
    private String email;
    private String school;
    private String studentId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static MemberResponseDto from(Member member) {
        return MemberResponseDto.builder()
                .id(member.getMemberId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .school(member.getSchool())
                .studentId(member.getStudentId())
                .createdAt(member.getCreatedAt())
                .modifiedAt(member.getModifiedAt())
                .build();
    }
}
