package efub.assignment.community.member.dto.response;

import efub.assignment.community.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

//member 생성 후 응답
@Builder @Getter
@AllArgsConstructor
public class MemberCreateResponseDto {
    private Long memberId;
    private Long studentId;
    private String university;
    private String nickname;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemberCreateResponseDto from(Member member){
        return MemberCreateResponseDto.builder()
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
