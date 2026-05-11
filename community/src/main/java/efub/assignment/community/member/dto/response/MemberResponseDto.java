package efub.assignment.community.member.dto.response;

import efub.assignment.community.member.domain.Member;

public record MemberResponseDto(
        Long memberId,
        String studentId,
        String university,
        String nickname,
        String email
) {
    public static MemberResponseDto from(Member member) {
        return new MemberResponseDto(
                member.getMemberId(),
                member.getStudentId(),
                member.getUniversity(),
                member.getNickname(),
                member.getEmail()
        );
    }
}