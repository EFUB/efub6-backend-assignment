package efub.assignment.community.member.controller;

import efub.assignment.community.member.dto.request.CreateMemberRequestDto;
import efub.assignment.community.member.dto.request.ProfileUpdateRequestDto;
import efub.assignment.community.member.dto.response.CreateMemberResponseDto;
import efub.assignment.community.member.dto.response.MemberResponseDto;
import efub.assignment.community.member.dto.response.UpdateProfileResponseDto;
import efub.assignment.community.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor //생성자 주입: final이나 @NotNull 붙은 애들
public class MemberController {

    private final MemberService memberService;

    // [멤버 조회]: /members/{memberId}
    //여기서 URI에서 아이디 받아서
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable("memberId") Long memberId) {
        //service 쪽에 아이디 전달해서 레포지토리에서 조회해 온 회원을 여기서 다시 받아 응답으로 만드는식?
        MemberResponseDto responseDto = memberService.getMember(memberId);

        return ResponseEntity.ok(responseDto);
    }

    // [멤버 생성]: /members
    @PostMapping
    public ResponseEntity<CreateMemberResponseDto> createMember(@RequestBody CreateMemberRequestDto requestDto) {
        //@RequestBody: MessageConverter가 클라이언트의 바디(JSON) -> dto로 변경해줌
       CreateMemberResponseDto responseDto = memberService.createMember(requestDto);

       return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // [프로필 수정]: /members/{memberId}
    @PatchMapping("/profile/{memberId}")
    public ResponseEntity<UpdateProfileResponseDto> updateMember(@PathVariable("memberId") Long memberId,
                                                          @RequestBody ProfileUpdateRequestDto requestDto) {
        UpdateProfileResponseDto responseDto = memberService.updateProfile(memberId, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    // [멤버 논리적 삭제/탈퇴]: /members/{memberId}
    @PatchMapping("/{memberId}")
    public ResponseEntity<Map<String, String>> unregisterMember(@PathVariable("memberId")Long memberId) {
        memberService.unregisterMember(memberId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "삭제가 완료되었습니다.");

        return ResponseEntity.ok(response);
    }

}
