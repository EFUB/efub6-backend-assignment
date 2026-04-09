package efub.assignment.community.member.controller;

import efub.assignment.community.member.dto.request.CreateMemberRequestDto;
import efub.assignment.community.member.dto.request.ProfileUpdateRequestDto;
import efub.assignment.community.member.dto.response.GetMemberResponseDto;
import efub.assignment.community.member.dto.response.MemberResponseDto;
import efub.assignment.community.member.service.MembersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MembersController {

    private final MembersService membersService;

    // 회원 조회: GET /members/{memberId}
    @GetMapping("/{memberId}")
    public ResponseEntity<GetMemberResponseDto> getMember(@PathVariable("memberId") Long memberId) {
        GetMemberResponseDto responseDto = membersService.getMember(memberId);
        return ResponseEntity.ok(responseDto);
    }

    // 회원 가입: POST /members
    @PostMapping
    public ResponseEntity<MemberResponseDto> createMember(@RequestBody CreateMemberRequestDto requestDto) {
        MemberResponseDto responseDto = membersService.createMember(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 회원 프로필 수정: PATCH /members/profile/{memberId}
    @PatchMapping("/profile/{memberId}")
    public ResponseEntity<MemberResponseDto> updateProfile(@PathVariable("memberId") Long memberId,
                                                           @RequestBody ProfileUpdateRequestDto requestDto) {
        MemberResponseDto responseDto = membersService.updateProfile(memberId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 회원 삭제
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Map<String, String>> deleteMember(@PathVariable("memberId") Long memberId) {
        membersService.deleteMember(memberId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "성공적으로 탈퇴되었습니다.");
        return ResponseEntity.ok(response);
    }
}
