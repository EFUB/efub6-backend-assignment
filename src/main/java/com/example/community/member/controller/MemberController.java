package com.example.community.member.controller;

import com.example.community.member.dto.request.CreateMemberRequestDto;
import com.example.community.member.dto.request.UpdateMemberRequestDto;
import com.example.community.member.dto.response.MemberResponseDto;
import com.example.community.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable("memberId") Long memberId) {
        MemberResponseDto responseDto = memberService.getMember(memberId);
        return ResponseEntity.ok(responseDto);
    }

    // 회원 생성
    @PostMapping
    public ResponseEntity<MemberResponseDto> createMember(@Valid @RequestBody CreateMemberRequestDto requestDto) {
        MemberResponseDto responseDto = memberService.createMember(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 회원 수정
    @PatchMapping("/profile/{memberId}")
    public ResponseEntity<MemberResponseDto> updateMember(@PathVariable Long memberId,
                                                          @Valid @RequestBody UpdateMemberRequestDto requestDto) {
        MemberResponseDto responseDto = memberService.updateMember(memberId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 회원 삭제 (논리 삭제)
    @PatchMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable("memberId") Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }

}