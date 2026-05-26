package efub.assignment.community.messageroom.service;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import efub.assignment.community.messageroom.domain.Message;
import efub.assignment.community.messageroom.domain.MessageRoom;
import efub.assignment.community.messageroom.dto.request.MessageRequestDto;
import efub.assignment.community.messageroom.dto.response.MessageListDto;
import efub.assignment.community.messageroom.dto.response.MessageResponseDto;
import efub.assignment.community.messageroom.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final MemberService memberService;
    private final MessageRoomService messageRoomService;

    @Transactional
    public MessageResponseDto createMessage(Long senderId, MessageRequestDto request, Long messageRoomId) {
        Member sender = memberService.findByMemberId(senderId);
        MessageRoom messageRoom = messageRoomService.findByMessageRoomId(messageRoomId);

        messageRoomService.authorizeMessageRoomMember(messageRoom, sender);

        Message message = Message.builder()
                .messageRoom(messageRoom)
                .sender(sender)
                .content(request.getContent())
                .build();
        messageRepository.save(message);

        return MessageResponseDto.of(message);
    }

    @Transactional (readOnly = true)
    public MessageListDto getMessageList(Long requesterId, Long messageRoomId) {
        Member requester = memberService.findByMemberId(requesterId);
        MessageRoom messageRoom = messageRoomService.findByMessageRoomId(messageRoomId);

        messageRoomService.authorizeMessageRoomMember(messageRoom, requester);

        List<Message> messages = messageRepository.findAllByMessageRoomOrderByCreatedAtAsc(messageRoom);
        Long receiverId = messageRoom.getCreator().equals(requester)
                ? messageRoom.getTarget().getMemberId()
                : messageRoom.getCreator().getMemberId();

        return MessageListDto.of(messages, messageRoom, requesterId, receiverId);
    }
}
