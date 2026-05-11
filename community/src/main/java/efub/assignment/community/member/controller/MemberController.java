package efub.assignment.community.member.controller;


import efub.assignment.community.member.dto.request.CreateMemberRequestDto;
import efub.assignment.community.member.dto.request.UpdateMemberNicknameRequestDto;
import efub.assignment.community.member.dto.response.CreateMemberResponseDto;
import efub.assignment.community.member.dto.response.MemberResponseDto;
import efub.assignment.community.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    // 멤버 생성
    @PostMapping
    public ResponseEntity<CreateMemberResponseDto> createMember(@RequestBody @Valid CreateMemberRequestDto requestDto){
        CreateMemberResponseDto responseDto = memberService.createMember(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 멤버 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable("memberId") Long memberId){
        MemberResponseDto responseDto = memberService.getMember(memberId);
        return ResponseEntity.ok(responseDto);
    }

    // 멤버 닉네임 수정
    @PatchMapping("/profile/{memberId}")
    public ResponseEntity<MemberResponseDto> updateMember(@PathVariable("memberId") Long memberId, @RequestBody @Valid UpdateMemberNicknameRequestDto requestDto){
        MemberResponseDto responseDto = memberService.updateMember(memberId,requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 멤버 논리적 삭제(탈퇴)
    @PatchMapping("/{memberId}")
    public ResponseEntity<Map<String, String>> deleteMember(@PathVariable("memberId") Long memberId){
        memberService.deleteMember(memberId);
        Map<String,String> response = new HashMap<>();
        response.put("message","성공적으로 탈퇴되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 멤버 물리적 삭제
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Map<String,String>> physicalDeleteMember(@PathVariable("memberId") Long memberId){
        memberService.physicalDeleteMember(memberId);
        Map<String,String> response = new HashMap<>();
        response.put("message","성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }

}
