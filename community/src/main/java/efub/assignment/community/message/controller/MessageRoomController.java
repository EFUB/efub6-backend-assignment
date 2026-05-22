package efub.assignment.community.message.controller;

import efub.assignment.community.message.dto.request.MessageRoomCreateRequest;
import efub.assignment.community.message.dto.response.MessageRoomCreateResponse;
import efub.assignment.community.message.service.MessageRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class MessageRoomController {

    private final MessageRoomService messageRoomService;

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
}