package efub.assignment.community.message.controller;

import efub.assignment.community.message.dto.request.MessageRoomCreateRequest;
import efub.assignment.community.message.dto.response.MessageRoomCreateResponse;
import efub.assignment.community.message.dto.response.MessageRoomExistResponse;
import efub.assignment.community.message.dto.response.MessageRoomListResponse;
import efub.assignment.community.message.service.MessageRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MessageRoomController {

    private final MessageRoomService messageRoomService;

    //쪽지방 생성
    @PostMapping("/posts/{postId}/message-rooms")
    public ResponseEntity<MessageRoomCreateResponse> createMessageRoom(
            @PathVariable Long postId,
            @RequestHeader("Auth-id") Long senderId,
            @Valid @RequestBody MessageRoomCreateRequest request
    ) {
        MessageRoomCreateResponse response =
                messageRoomService.createMessageRoom(postId, senderId, request);

        return ResponseEntity
                .created(URI.create("/message-rooms/" + response.getMessageRoomId()))
                .body(response);
    }

    //쪽지방 여부 조회
    @GetMapping("/message-rooms/exist")
    public ResponseEntity<?> existMessageRoom(
            @RequestHeader("Auth-id") Long memberId,
            @RequestParam Long postId
    ) {
        Optional<MessageRoomExistResponse> response =
                messageRoomService.existMessageRoom(memberId, postId);

        if (response.isPresent()) {
            return ResponseEntity.ok(response.get());
        }

        return ResponseEntity.ok(List.of());
    }

    //쪽지방 목록 조회
    @GetMapping("/message-rooms/{memberId}")
    public ResponseEntity<MessageRoomListResponse> getMessageRooms(
            @PathVariable Long memberId
    ) {
        MessageRoomListResponse response = messageRoomService.getMessageRooms(memberId);
        return ResponseEntity.ok(response);
    }

    //쪽지방 삭제
    @DeleteMapping("/message-rooms/{messageRoomId}")
    public ResponseEntity<Void> deleteMessageRoom(
            @PathVariable Long messageRoomId
    ) {
        messageRoomService.deleteMessageRoom(messageRoomId);
        return ResponseEntity.noContent().build();
    }
}