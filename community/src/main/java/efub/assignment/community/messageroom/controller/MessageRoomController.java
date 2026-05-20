package efub.assignment.community.messageroom.controller;

import efub.assignment.community.messageroom.dto.request.MessageRequestDto;
import efub.assignment.community.messageroom.dto.request.MessageRoomRequest;
import efub.assignment.community.messageroom.dto.request.MessageRoomCreateDto;
import efub.assignment.community.messageroom.dto.response.*;
import efub.assignment.community.messageroom.service.MessageRoomService;
import efub.assignment.community.messageroom.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messagerooms")
@RequiredArgsConstructor
public class MessageRoomController {

    private final MessageRoomService messageRoomService;
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageRoomDto> createMessageRoom(@RequestHeader("Auth-id") Long requesterId,
                                                            @RequestBody MessageRoomCreateDto request) {
        MessageRoomDto response = messageRoomService.createMessageRoom(requesterId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<MessageRoomListDto> getMessageRoomList(@RequestHeader("Auth-id") Long requesterID) {
        MessageRoomListDto response = messageRoomService.getMessageRoomList(requesterID);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<CheckMessageRoomDto> checkMessageRoomExist(@RequestHeader("Auth-id") Long requesterId,
                                                                     @RequestBody MessageRoomRequest request) {
        CheckMessageRoomDto response = messageRoomService.checkMessageRoomExist(requesterId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMessageRoom (@RequestHeader("Auth-id") Long requesterId,
                                                   @RequestBody MessageRoomRequest request) {
        messageRoomService.deleteMessageRoom(requesterId, request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{messageRoomId}/messages")
    public ResponseEntity<MessageResponseDto> createMessage (@RequestHeader("Auth-id") Long senderId,
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
