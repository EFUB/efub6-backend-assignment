package efub.assignment.community.member.dto.response;

import efub.assignment.community.member.domain.Member;

public record CreateMemberResponseDto(
        Long memberId,
        String studentId,
        String university,
        String nickname,
        String email
) {
    public static CreateMemberResponseDto from(Member member) {
        return new CreateMemberResponseDto(
                member.getMemberId(),
                member.getStudentId(),
                member.getUniversity(),
                member.getNickname(),
                member.getEmail()
        );
    }
}