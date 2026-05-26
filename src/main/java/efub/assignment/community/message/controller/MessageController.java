package efub.assignment.community.message.controller;

import efub.assignment.community.message.dto.request.CreateMessageRequest;
import efub.assignment.community.message.dto.response.MessageListResponse;
import efub.assignment.community.message.dto.response.MessageResponse;
import efub.assignment.community.message.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messageRooms/{messageRoomId}/messages")
public class MessageController {
    private final MessageService messageService;

    // 쪽지 생성
    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(@PathVariable("messageRoomId") Long messageRoomId,
                                                         @RequestHeader("Auth-Id") Long senderId,
                                                         @Valid @RequestBody CreateMessageRequest request) {
        MessageResponse response = messageService.createMessage(messageRoomId, senderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 쪽지 목록 조회
    @GetMapping
    public ResponseEntity<MessageListResponse> getAllMessages(@PathVariable("messageRoomId") Long messageRoomId,
                                                              @RequestHeader("Auth-Id") Long member_id) {
        MessageListResponse response = messageService.getAllMessages(messageRoomId, member_id);
        return ResponseEntity.ok(response);
    }
}
