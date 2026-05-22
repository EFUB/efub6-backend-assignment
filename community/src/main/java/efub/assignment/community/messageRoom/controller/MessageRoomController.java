package efub.assignment.community.messageRoom.controller;

import efub.assignment.community.messageRoom.dto.request.CreateMessageRoomRequest;
import efub.assignment.community.messageRoom.dto.response.CreateMessageRoomResponse;
import efub.assignment.community.messageRoom.dto.response.MessageRoomResponse;
import efub.assignment.community.messageRoom.service.MessageRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messageRooms")
public class MessageRoomController {

    private final MessageRoomService messageRoomService;

    // 쪽지방 생성
    @PostMapping
    public ResponseEntity<CreateMessageRoomResponse> createMessageRoom(@RequestHeader("Auth-Id") Long senderId,
                                                                        @RequestBody @Valid CreateMessageRoomRequest request) {
        CreateMessageRoomResponse response = messageRoomService.createMessageRoom(senderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 쪽지방 여부 확인
    @GetMapping
    public ResponseEntity<?> getExistMessageRoom(@RequestHeader("Auth-Id") Long senderId,
                                                                       @RequestParam("receiverId") Long receiverId,
                                                                       @RequestParam("postId") Long postId) {
        return ResponseEntity.ok(messageRoomService.getExistMessageRoom(senderId, receiverId, postId));
    }

    // 특정 회원의 모든 쪽지방 조회
    @GetMapping("/list")
    public ResponseEntity<List<MessageRoomResponse>> getMessageRoomList(@RequestHeader("Auth-Id") Long requesterId) {
        return ResponseEntity.ok(messageRoomService.getMessageRoomList(requesterId));
    }

    // 쪽지방 삭제
    @DeleteMapping("/{messageRoomId}")
    public ResponseEntity<Void> deleteMessageRoom(@PathVariable("messageRoomId") Long messageRoomId,
                                                  @RequestHeader("Auth-Id") Long requesterId) {
        messageRoomService.deleteMessageRoom(messageRoomId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
