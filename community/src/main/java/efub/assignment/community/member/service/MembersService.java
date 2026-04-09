package efub.assignment.community.member.service;

import efub.assignment.community.member.dto.request.CreateMemberRequestDto;
import efub.assignment.community.member.dto.request.ProfileUpdateRequestDto;
import efub.assignment.community.member.dto.response.GetMemberResponseDto;
import efub.assignment.community.member.dto.response.MemberResponseDto;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembersService {

    private final MemberRepository memberRepository;

    // 회원 조회
    public GetMemberResponseDto getMember(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        return GetMemberResponseDto.from(member);
    }

    // 회원 가입
    @Transactional
    public MemberResponseDto createMember(CreateMemberRequestDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 email 입니다." + requestDto.getEmail());
        }
        Member member = requestDto.toEntity();
        Member savedMember = memberRepository.save(member);
        return MemberResponseDto.from(savedMember);
    }

    // 회원 프로필 수정
    @Transactional
    public MemberResponseDto updateProfile(Long memberId, ProfileUpdateRequestDto requestDto) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        member.updateProfile(requestDto.getNickname());
        return MemberResponseDto.from(member);
    }

    // 회원 삭제
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        memberRepository.delete(member);
    }
}
