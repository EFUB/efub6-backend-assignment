package efub.assignment.community.messageroom.controller;

import efub.assignment.community.messageroom.dto.request.MessageRequestDto;
import efub.assignment.community.messageroom.dto.response.MessageListDto;
import efub.assignment.community.messageroom.dto.response.MessageResponseDto;
import efub.assignment.community.messageroom.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    @PostMapping("/{messageRoomId}/messages")
    public ResponseEntity<MessageResponseDto> createMessage (@Valid @RequestHeader("Auth-id") Long senderId,
                                                             @RequestBody MessageRequestDto request,
                                                             @PathVariable("messageRoomId") Long messageRoomId) {
        MessageResponseDto response = messageService.createMessage(senderId, request, messageRoomId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{messageRoomId}")
    public ResponseEntity<MessageListDto> getMessageList (@RequestHeader("Auth-id") Long requesterId,
                                                          @PathVariable("messageRoomId") Long messageRoomId) {
        MessageListDto response = messageService.getMessageList(requesterId, messageRoomId);

        return ResponseEntity.ok(response);
    }
}
