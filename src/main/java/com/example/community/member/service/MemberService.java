package com.example.community.member.service;

import com.example.community.global.exception.CustomException;
import com.example.community.global.exception.ErrorCode;
import com.example.community.member.domain.Member;
import com.example.community.member.domain.MemberStatus;
import com.example.community.member.dto.request.CreateMemberRequestDto;
import com.example.community.member.dto.request.UpdateMemberRequestDto;
import com.example.community.member.dto.response.MemberResponseDto;
import com.example.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 단건 조회
    @Transactional(readOnly = true)
    public MemberResponseDto getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberResponseDto.from(member);
    }

    // 회원 생성
    @Transactional
    public MemberResponseDto createMember(CreateMemberRequestDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.email())) {
            throw new CustomException(ErrorCode.MEMBER_EMAIL_ALREADY_EXISTS);
        }

        Member member = requestDto.toEntity();
        Member savedMember = memberRepository.save(member);

        return MemberResponseDto.from(savedMember);
    }

    // 회원 정보 수정
    @Transactional
    public MemberResponseDto updateMember(Long memberId, UpdateMemberRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (requestDto.email() != null && !requestDto.email().equals(member.getEmail())
                && memberRepository.existsByEmail(requestDto.email())) {
            throw new CustomException(ErrorCode.MEMBER_EMAIL_ALREADY_EXISTS);
        }

        member.updateProfile(requestDto.email(), requestDto.nickname());

        return MemberResponseDto.from(member);
    }

    // 회원 논리적 삭제
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        member.changeStatus(MemberStatus.UNREGISTER);
    }

    @Transactional(readOnly = true)
    public Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
