package efub.assignment.community.message.controller;

import efub.assignment.community.global.security.AuthenticatedMember;
import efub.assignment.community.message.dto.request.CreateMessageRequestDto;
import efub.assignment.community.message.dto.response.CreateMessageResponseDto;
import efub.assignment.community.message.dto.response.MessageListResponseDto;
import efub.assignment.community.message.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message-rooms/{messageRoomId}/messages")
public class MessageController {

    private final MessageService messageService;

    // 쪽지 생성
    @PostMapping
    public ResponseEntity<CreateMessageResponseDto> createMessage(
            @PathVariable("messageRoomId") Long messageRoomId,
            @AuthenticationPrincipal AuthenticatedMember authenticatedMember,
            @RequestBody @Valid CreateMessageRequestDto requestDto
    ) {
        CreateMessageResponseDto responseDto =
                messageService.createMessage(
                        messageRoomId,
                        authenticatedMember.memberId(),
                        requestDto
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 쪽지방 메시지 전체 조회
    @GetMapping
    public ResponseEntity<MessageListResponseDto> getMessages(
            @PathVariable("messageRoomId") Long messageRoomId,
            @AuthenticationPrincipal AuthenticatedMember authenticatedMember
    ) {
        MessageListResponseDto responseDto =
                messageService.getMessages(messageRoomId, authenticatedMember.memberId());

        return ResponseEntity.ok(responseDto);
    }
}
