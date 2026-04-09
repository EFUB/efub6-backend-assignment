package efub.assignment.community.member.dto.response;

import efub.assignment.community.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

//멤버 조회 응답
@Builder @Getter
@AllArgsConstructor
public class MemberResponseDto {
    private Long memberId;
    private Long studentId;
    private String university;
    private String nickname;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemberResponseDto from(Member member){
        return MemberResponseDto.builder()
                .memberId(member.getMemberId())
                .studentId(member.getStudentId())
                .university(member.getUniversity())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
