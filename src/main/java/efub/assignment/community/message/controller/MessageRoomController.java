package efub.assignment.community.message.controller;

import efub.assignment.community.message.dto.request.CreateMessageRoomRequest;
import efub.assignment.community.message.dto.response.MessageRoomListResponse;
import efub.assignment.community.message.dto.response.MessageRoomResponse;
import efub.assignment.community.message.service.MessageRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messageRooms")
public class MessageRoomController {
    private final MessageRoomService messageRoomService;

    // 쪽지방 생성
    @PostMapping
    public ResponseEntity<MessageRoomResponse> createMessageRoom(@RequestHeader("Auth-Id") Long senderId,
                                                                 @Valid @RequestBody CreateMessageRoomRequest request) {
        MessageRoomResponse response = messageRoomService.createMessageRoom(senderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 쪽지방 여부 조회
    @GetMapping("/check")
    public ResponseEntity<Long> checkMessageRoomExists(@RequestHeader("Auth-Id") Long senderId,
                                                       @RequestParam("receiverId") Long receiverId,
                                                       @RequestParam("postId") Long postId) {
        Long messageRoomId = messageRoomService.checkMessageRoomExists(senderId, receiverId, postId);

        return ResponseEntity.ok(messageRoomId);
    }

    // 쪽지방 목록 조회
    @GetMapping
    public ResponseEntity<MessageRoomListResponse> getAllMessageRooms(@RequestHeader("Auth-Id") Long memberId) {
        return ResponseEntity.ok(messageRoomService.getAllMessageRooms(memberId));
    }

    // 쪽지방 삭제
    @DeleteMapping("/{messageRoomId}")
    public ResponseEntity<Void> deleteMessageRoom(@PathVariable("messageRoomId") Long messageRoomId,
                                                  @RequestHeader("Auth-Id") Long member_id) {
        messageRoomService.deleteMessageRoom(messageRoomId, member_id);
        return ResponseEntity.noContent().build();
    }

}
