package efub.assignment.community.member.dto.response;

import efub.assignment.community.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder @Getter
@AllArgsConstructor
public class UpdateProfileResponseDto {
    private String email;
    private String nickname;
    private String university;
    private String studentId;

    public static UpdateProfileResponseDto from (Member member) {
        return UpdateProfileResponseDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .university(member.getUniversity())
                .studentId(member.getStudentId())
                .build();
    }
}
