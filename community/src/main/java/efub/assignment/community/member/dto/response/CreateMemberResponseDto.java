package efub.assignment.community.member.dto.response;

import efub.assignment.community.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class CreateMemberResponseDto {
    private Long memberId;
    private String studentId;
    private String university;
    private String nickname;
    private String email;

    public static CreateMemberResponseDto from(Member member){
        return CreateMemberResponseDto.builder()
                .memberId(member.getMemberId())
                .studentId(member.getStudentId())
                .university(member.getUniversity())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }
}
