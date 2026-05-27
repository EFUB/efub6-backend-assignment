package efub.assignment.community.messageroom.controller;

import efub.assignment.community.messageroom.dto.request.MessageRoomRequestDto;
import efub.assignment.community.messageroom.dto.request.CreateMessageRoomDto;
import efub.assignment.community.messageroom.dto.response.*;
import efub.assignment.community.messageroom.service.MessageRoomService;
import efub.assignment.community.messageroom.service.MessageService;
import jakarta.validation.Valid;
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
    public ResponseEntity<MessageRoomDto> createMessageRoom(@Valid @RequestHeader("Auth-id") Long requesterId,
                                                            @RequestBody CreateMessageRoomDto request) {
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
                                                                     @Valid @RequestBody MessageRoomRequestDto request) {
        CheckMessageRoomDto response = messageRoomService.checkMessageRoomExist(requesterId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMessageRoom (@RequestHeader("Auth-id") Long requesterId,
                                                   @Valid @RequestBody MessageRoomRequestDto request) {
        messageRoomService.deleteMessageRoom(requesterId, request);

        return ResponseEntity.noContent().build();
    }
}
