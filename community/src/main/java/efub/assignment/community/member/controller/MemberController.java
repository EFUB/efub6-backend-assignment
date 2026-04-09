package efub.assignment.community.member.controller;

import efub.assignment.community.member.dto.request.MemberCreateRequestDto;
import efub.assignment.community.member.dto.request.MemberUpdateRequestDto;
import efub.assignment.community.member.dto.response.MemberCreateResponseDto;
import efub.assignment.community.member.dto.response.MemberResponseDto;
import efub.assignment.community.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    //회원 생성 POST /members
    @PostMapping
    public ResponseEntity<MemberCreateResponseDto> createMember(@RequestBody MemberCreateRequestDto requestDto){
        MemberCreateResponseDto responseDto = memberService.createMember(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    //회원 조회 GET /members/{memberId}
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable("memberId") Long memberId){
        MemberResponseDto responseDto = memberService.getMember(memberId);
        return ResponseEntity.ok(responseDto);
    }

    //회원 수정 PATCH /members/{memberId}
    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> updateMember(@PathVariable("memberId") Long memberId, @RequestBody MemberUpdateRequestDto requestDto){
            MemberResponseDto responseDto = memberService.updateMember(memberId, requestDto);
            return ResponseEntity.ok(responseDto);
    }

    //회원 삭제 DELETE /members/{memberId}
    @DeleteMapping("/{memberId}")
    public ResponseEntity<String> deleteMember(@PathVariable("memberId") Long memberId){
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}
