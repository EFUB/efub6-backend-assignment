package efub.assignment.community.member.dto.response;

import efub.assignment.community.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder @Getter
@AllArgsConstructor
public class CreateMemberResponseDto {
    private String nickname;
    private String email;
    private String university;
    private String studentId;

    public static CreateMemberResponseDto from(Member member) {
        return CreateMemberResponseDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .university(member.getUniversity())
                .studentId(member.getStudentId())
                .build();
    }
}
