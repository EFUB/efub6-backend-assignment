package efub.assignment.community.member.service;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.dto.request.MemberCreateRequestDto;
import efub.assignment.community.member.dto.request.MemberUpdateRequestDto;
import efub.assignment.community.member.dto.response.MemberCreateResponseDto;
import efub.assignment.community.member.dto.response.MemberResponseDto;
import efub.assignment.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    //멤버 조회
    public MemberResponseDto getMember(Long memberId){
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        return MemberResponseDto.from(member);
    }

    //멤버 생성
    @Transactional
    public MemberCreateResponseDto createMember(MemberCreateRequestDto requestDto){
        if(memberRepository.existsByEmail(requestDto.getEmail())){
            throw new IllegalArgumentException("이미 존재하는 email입니다."+requestDto.getEmail());
        }
        if(memberRepository.existsByStudentId(requestDto.getStudentId())){
            throw new IllegalArgumentException("이미 존재하는 학번입니다."+requestDto.getStudentId());
        }
        Member member = requestDto.toEntity();
        Member savedMember = memberRepository.save(member);
        return MemberCreateResponseDto.from(savedMember);
    }

    //멤버 수정
    @Transactional
    public MemberResponseDto updateMember(Long memberId, MemberUpdateRequestDto requestDto){
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        member.updateMember(requestDto.getNickname(), requestDto.getEmail(), requestDto.getPassword());
        Member updateMember = memberRepository.save(member);
        return MemberResponseDto.from(updateMember);
    }

    //멤버 삭제(물리적 삭제-Delete)
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        memberRepository.delete(member);

    }


}
