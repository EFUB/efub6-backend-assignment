package efub.assignment.community.messageRoom.controller;

import efub.assignment.community.messageRoom.dto.request.CreateMessageRoomRequestDto;
import efub.assignment.community.messageRoom.dto.response.CreateMessageRoomResponseDto;
import efub.assignment.community.messageRoom.dto.response.MessageRoomCheckResponseDto;
import efub.assignment.community.messageRoom.dto.response.MessageRoomListResponseDto;
import efub.assignment.community.messageRoom.service.MessageRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MessageRoomController {
    private final MessageRoomService messageRoomService;

    // 쪽지방 생성
    @PostMapping("/message-rooms")
    public ResponseEntity<CreateMessageRoomResponseDto> createMessageRoom(
            @RequestHeader("Auth-Id") Long creatorId,
            @RequestBody @Valid CreateMessageRoomRequestDto requestDto
    ) {
        CreateMessageRoomResponseDto responseDto =
                messageRoomService.createMessageRoom(creatorId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 쪽지방 목록 조회
    @GetMapping("/members/{memberId}/message-rooms")
    public ResponseEntity<MessageRoomListResponseDto> getMessageRoomByMember(
            @RequestHeader("Auth-Id") Long authId,
            @PathVariable Long memberId
    ) {
        MessageRoomListResponseDto responseDto =
                messageRoomService.getMessageRoomByMember(authId, memberId);

        return ResponseEntity.ok(responseDto);
    }

    // 쪽지방 여부 조회
    @GetMapping("/message-rooms")
    public ResponseEntity<MessageRoomCheckResponseDto> getMessageRoom(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam Long postId
    ) {
        MessageRoomCheckResponseDto responseDto =
                messageRoomService.getMessageRoom(senderId, receiverId, postId);

        return ResponseEntity.ok(responseDto);
    }

    // 쪽지방 삭제
    @DeleteMapping("/message-rooms/{messageRoomId}")
    public ResponseEntity<Void> deleteMessageRoom(
            @PathVariable Long messageRoomId,
            @RequestHeader("Auth-Id") Long memberId
    ) {
        messageRoomService.deleteMessageRoom(messageRoomId, memberId);
        return ResponseEntity.noContent().build();
    }
}
