package efub.assignment.community.member.service;

import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.domain.MemberStatus;
import efub.assignment.community.member.dto.request.CreateMemberRequestDto;
import efub.assignment.community.member.dto.request.UpdateMemberNicknameRequestDto;
import efub.assignment.community.member.dto.response.CreateMemberResponseDto;
import efub.assignment.community.member.dto.response.MemberResponseDto;
import efub.assignment.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    // 멤버 생성
    @Transactional
    public CreateMemberResponseDto createMember(CreateMemberRequestDto requestDto){
        if(memberRepository.existsByEmail(requestDto.email())){
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        Member member = requestDto.toEntity();
        Member savedMember = memberRepository.save(member);
        return CreateMemberResponseDto.from(savedMember);
    }

    // 멤버 조회
    @Transactional(readOnly = true)
    public MemberResponseDto getMember(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberResponseDto.from(member);
    }

    // 멤버 닉네임 엡데이트
    @Transactional
    public MemberResponseDto updateMember(Long memberId, UpdateMemberNicknameRequestDto requestDto){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        member.updateNickname(requestDto.nickname());
        Member updatedMember = memberRepository.save(member);
        return MemberResponseDto.from(updatedMember);
    }

    // 멤버 논리적 삭제
    @Transactional
    public void deleteMember(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        member.changeStatus(MemberStatus.UNREGISTER);
        memberRepository.save(member);
    }

    // 멤버 물리적 삭제
    @Transactional
    public void physicalDeleteMember(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        memberRepository.delete(member);
    }
}
