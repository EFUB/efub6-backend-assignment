package efub.assignment.community.member.service;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.domain.MemberStatus;
import efub.assignment.community.member.dto.CreateMemberRequestDto;
import efub.assignment.community.member.dto.MemberResponseDto;
import efub.assignment.community.member.dto.UpdateMemberRequestDto;
import efub.assignment.community.member.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MembersService {

    private final MemberRepository membersRepository;

    // 멤버 단건 조회
    public MemberResponseDto getMember(Long memberId){
        Member member = membersRepository.findByMemberId(memberId)
                .orElseThrow(()-> new IllegalArgumentException("해당 멤버를 찾을 수 없습니다."));
        return MemberResponseDto.from(member);
    }

    @Transactional
    public MemberResponseDto createMember(CreateMemberRequestDto requestDto) {
        // 이메일 중복 검사
        if(membersRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 email입니다. "+requestDto.getEmail());
        }
        Member member = requestDto.toEntity();
        Member savedMember = membersRepository.save(member);
        return MemberResponseDto.from(savedMember);
    }

    // 멤버 닉네임 수정
    @Transactional
    public MemberResponseDto updateMember(Long memberId, UpdateMemberRequestDto requestDto) {
        Member member = membersRepository.findByMemberId(memberId)
                .orElseThrow(()->new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        member.updateNickname(requestDto.getNickname());
        Member updatedMember = membersRepository.save(member);
        return MemberResponseDto.from(updatedMember);
    }

    // 멤버 논리적 삭제 (status 변경)
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = membersRepository.findByMemberId(memberId)
                .orElseThrow(()->new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        member.changeStatus(MemberStatus.UNREGISTER);
        membersRepository.save(member);
    }

    // 멤버 찾아주는 도우미 메서드 추가
    @Transactional(readOnly = true)
    public Member findByMemberId(Long memberId) {
        return membersRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public Member findByNickname(String nickname) {
        return membersRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임을 가진 회원을 찾을 수 없습니다. 닉네임: " + nickname));
    }
}
