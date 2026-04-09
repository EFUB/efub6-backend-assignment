package efub.assignment.community.member.service;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.domain.MemberStatus;
import efub.assignment.community.member.dto.request.CreateMemberRequestDto;
import efub.assignment.community.member.dto.request.ProfileUpdateRequestDto;
import efub.assignment.community.member.dto.response.CreateMemberResponseDto;
import efub.assignment.community.member.dto.response.MemberResponseDto;
import efub.assignment.community.member.dto.response.UpdateProfileResponseDto;
import efub.assignment.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor //final, @NotNull 주입.
@Transactional(readOnly = true) //클래스 내 모든 메소드에 트랜잭션 기능 적용
public class MemberService {
    //Service 계층에서 Repository 필드로 가짐
    private final MemberRepository memberRepository;

    // [멤버 조회]
    public MemberResponseDto getMember(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("No message available"));

        //멤버를 바로 전달하는게 아니고,Dto의 메소드의 인자?로
        // 전달해서 Dto 형태로 컨트롤러에 전달
        return MemberResponseDto.from(member);
    }

    // [멤버 생성]
    @Transactional
    public CreateMemberResponseDto createMember(CreateMemberRequestDto requestDto) {
        if (memberRepository.existsByStudentId(requestDto.getStudentId())) {
            throw new IllegalArgumentException("No message available");
        }
        Member member = requestDto.toEntity();
        Member savedMember = memberRepository.save(member);

        return CreateMemberResponseDto.from(savedMember);
    }

    // [프로필 수정]
    @Transactional
    public UpdateProfileResponseDto updateProfile(Long accountId, ProfileUpdateRequestDto requestDto) {
        Member member = memberRepository.findByMemberId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No message available"));

        member.updateProfile(requestDto.getEmail(), requestDto.getPassword(), requestDto.getNickname(),
                requestDto.getUniversity(), requestDto.getStudentId());

        return UpdateProfileResponseDto.from(member);
    }

    // [멤버 탈퇴/논리적 삭제]
    @Transactional
    public void unregisterMember(Long accountId) {
        Member member = memberRepository.findByMemberId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No message available"));

        member.changeStatus(MemberStatus.UNREGISTER);
    }
}
