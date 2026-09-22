package com.example.community.member.service;

import com.example.community.global.exception.CustomException;
import com.example.community.global.exception.ErrorCode;
import com.example.community.member.domain.Member;
import com.example.community.member.domain.MemberStatus;
import com.example.community.member.dto.request.CreateMemberRequestDto;
import com.example.community.member.dto.request.UpdateMemberRequestDto;
import com.example.community.member.dto.response.CreateMemberResponseDto;
import com.example.community.member.dto.response.MemberResponseDto;
import com.example.community.member.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 단건 조회
    @Transactional(readOnly = true)
    public MemberResponseDto getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        return MemberResponseDto.from(member);
    }

    // 회원 생성
    @Transactional
    public CreateMemberResponseDto createMember(CreateMemberRequestDto requestDto) {
        // 이메일 중복 검사
        if (memberRepository.existsByEmail(requestDto.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        Member member = requestDto.toEntity();
        Member savedMember = memberRepository.save(member);

        return CreateMemberResponseDto.from(savedMember);
    }

    // 회원 정보 수정
    @Transactional
    public MemberResponseDto updateMember(Long memberId, @Valid UpdateMemberRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        member.updateProfile(requestDto);

        return MemberResponseDto.from(member);
    }

    // 회원 논리적 삭제
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        member.changeStatus(MemberStatus.UNREGISTER);
    }

    public Member findByMemberId(Long memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
