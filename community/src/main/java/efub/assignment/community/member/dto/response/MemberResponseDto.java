package efub.assignment.community.member.dto.response;

import efub.assignment.community.member.domain.Member;

public record MemberResponseDto(
        Long memberId,
        String nickname,
        String email,
        String profileImage
) {
    public static MemberResponseDto from(Member member) {
        return new MemberResponseDto(
                member.getMemberId(),
                member.getNickname(),
                member.getEmail(),
                member.getProfileImage()
        );
    }
}
