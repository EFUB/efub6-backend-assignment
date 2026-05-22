package efub.assignment.community.message.controller;

import efub.assignment.community.message.dto.request.MessageCreateRequest;
import efub.assignment.community.message.dto.response.MessageCreateResponse;
import efub.assignment.community.message.dto.response.MessageListResponse;
import efub.assignment.community.message.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/message-rooms/{messageRoomId}/messages")
    public ResponseEntity<MessageCreateResponse> createMessage(
            @PathVariable Long messageRoomId,
            @RequestHeader("Auth-id") Long senderId,
            @Valid @RequestBody MessageCreateRequest request
    ) {
        MessageCreateResponse response =
                messageService.createMessage(messageRoomId, senderId, request);

        return ResponseEntity
                .created(URI.create("/message-rooms/" + messageRoomId + "/messages"))
                .body(response);
    }

    @GetMapping("/message-rooms/{messageRoomId}/messages")
    public ResponseEntity<MessageListResponse> getMessages(
            @PathVariable Long messageRoomId,
            @RequestHeader("Auth-id") Long memberId
    ) {
        MessageListResponse response =
                messageService.getMessages(messageRoomId, memberId);

        return ResponseEntity.ok(response);
    }
}