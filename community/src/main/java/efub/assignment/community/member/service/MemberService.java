package efub.assignment.community.member.service;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.domain.MemberStatus;
import efub.assignment.community.member.dto.request.CreateMemberRequestDto;
import efub.assignment.community.member.dto.request.NicknameUpdateRequestDto;
import efub.assignment.community.member.dto.response.CreateMemberResponseDto;
import efub.assignment.community.member.dto.response.MemberResponseDto;
import efub.assignment.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    // 멤버 생성
    public CreateMemberResponseDto createMember(CreateMemberRequestDto requestDto){
        if(memberRepository.existsByEmail(requestDto.getEmail())){
            throw new IllegalArgumentException("이미 존재하 email입니다."+requestDto.getEmail());
        }
        Member member = requestDto.toEntity();
        Member savedMember = memberRepository.save(member);
        return CreateMemberResponseDto.from(savedMember);
    }

    // 멤버 조회
    public MemberResponseDto getMember(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        return MemberResponseDto.from(member);
    }

    // 멤버 닉네임 엡데이트
    public MemberResponseDto updateMember(Long memberId, NicknameUpdateRequestDto requestDto){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        member.updateNickname(requestDto.getNickname());
        Member updatedMember = memberRepository.save(member);
        return MemberResponseDto.from(updatedMember);
    }

    // 멤버 논리적 삭제
    public void deleteMember(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        member.changeStatus(MemberStatus.UNREGISTER);
        memberRepository.save(member);
    }

    // 멤버 물리적 삭제
    public void physicalDeleteMember(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        memberRepository.delete(member);
    }
}
