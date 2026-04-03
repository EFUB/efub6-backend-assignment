package efub.assignment.community.member.dto.response;

import efub.assignment.community.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder @Getter
//클래스의 모든 필드를 매개변수로 받는 생성자 자동으로 만듦
@AllArgsConstructor
//멤버 생성 응답 DTO
public class MemberResponseDto {
    private Long memberId;
    private String email;
    private String nickname;
    private String university;
    private String studentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemberResponseDto from (Member member) {
        return MemberResponseDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .university(member.getUniversity())
                .studentId(member.getStudentId())
                .createdAt((member.getCreatedAt()))
                .updatedAt(member.getUpdatedAt())
                .build();
    }

}
