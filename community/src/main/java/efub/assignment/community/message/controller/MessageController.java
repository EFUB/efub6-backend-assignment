package efub.assignment.community.message.controller;

import efub.assignment.community.message.dto.request.CreateMessageRequest;
import efub.assignment.community.message.dto.response.CreateMessageResponse;
import efub.assignment.community.message.dto.response.GetMessageListResponse;
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
    public ResponseEntity<CreateMessageResponse> createMessage(@PathVariable("messageRoomId") Long messageRoomId,
                                                               @RequestHeader("Auth-Id") Long senderId,
                                                               @RequestBody @Valid CreateMessageRequest request) {
        CreateMessageResponse response = messageService.createMessage(messageRoomId, senderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 쪽지방 모든 메시지 조회
    @GetMapping("/list")
    public ResponseEntity<GetMessageListResponse> getMessageList(@RequestHeader("Auth-Id") Long requesterId,
                                                                 @PathVariable("messageRoomId") Long messageRoomId) {
        GetMessageListResponse response = messageService.getMessageList(messageRoomId, requesterId);
        return ResponseEntity.ok(response);
    }
}
