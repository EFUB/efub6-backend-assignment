package efub.assignment.community.member.dto.response;

import efub.assignment.community.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class MemberResponseDto {
    private Long memberId;
    private String studentId;
    private String university;
    private String nickname;
    private String email;

    public static MemberResponseDto from(Member member){
        return MemberResponseDto.builder()
                .memberId(member.getMemberId())
                .studentId(member.getStudentId())
                .university(member.getUniversity())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }
}
