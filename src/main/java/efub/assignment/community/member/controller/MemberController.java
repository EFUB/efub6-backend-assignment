package efub.assignment.community.member.controller;

import efub.assignment.community.member.dto.CreateMemberRequestDto;
import efub.assignment.community.member.dto.MemberResponseDto;
import efub.assignment.community.member.dto.UpdateMemberRequestDto;
import efub.assignment.community.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService membersService;

    // 생성자 주입 (DI)
    public MemberController(MemberService membersService) {
        this.membersService = membersService;
    }

    @PostMapping
    public ResponseEntity<MemberResponseDto> createMember(@RequestBody @Valid CreateMemberRequestDto requestDto) {
        MemberResponseDto responseDto = membersService.createMember(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable("memberId") Long memberId) {
        MemberResponseDto responseDto = membersService.getMember(memberId);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/profile/{memberId}")
    public ResponseEntity<MemberResponseDto> updateMember(@PathVariable("memberId") Long memberId,
                                                          @RequestBody @Valid UpdateMemberRequestDto requestDto) {
        MemberResponseDto responseDto = membersService.updateMember(memberId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<String> deleteMember(@PathVariable("memberId") Long memberId) {
        membersService.deleteMember(memberId);
        return ResponseEntity.ok("message: 성공적으로 탈퇴되었습니다.");
    }
}
