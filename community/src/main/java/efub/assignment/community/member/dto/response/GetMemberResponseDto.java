package efub.assignment.community.member.dto.response;

import efub.assignment.community.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// Member 생성 후 응답 DTO

@Builder @Getter
@AllArgsConstructor
public class GetMemberResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String university;
    private String studentId;

    public static GetMemberResponseDto from(Member member) {
        return GetMemberResponseDto.builder()
                .id(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .university(member.getUniversity())
                .studentId(member.getStudentId())
                .build();
    }
}
