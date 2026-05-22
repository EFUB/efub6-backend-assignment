package efub.assignment.community.message.service;

import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.domain.MessageRoom;
import efub.assignment.community.message.dto.request.MessageCreateRequest;
import efub.assignment.community.message.dto.response.MessageCreateResponse;
import efub.assignment.community.message.dto.response.MessageListResponse;
import efub.assignment.community.message.dto.summary.MessageSummary;
import efub.assignment.community.message.repository.MessageRepository;
import efub.assignment.community.message.repository.MessageRoomRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MemberService memberService;
    private final MessageRoomRepository messageRoomRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public MessageCreateResponse createMessage(
            Long messageRoomId,
            Long senderId,
            @Valid MessageCreateRequest request
    ) {
        MessageRoom messageRoom = findByMessageRoomId(messageRoomId);
        Member sender = memberService.findByMemberId(senderId);

        validateMessageRoomMember(messageRoom, sender);

        Message message = Message.builder()
                .messageRoom(messageRoom)
                .sender(sender)
                .content(request.getContent())
                .build();

        Message savedMessage = messageRepository.save(message);

        return MessageCreateResponse.from(savedMessage);
    }

    @Transactional(readOnly = true)
    public MessageListResponse getMessages(
            Long messageRoomId,
            Long memberId
    ) {
        MessageRoom messageRoom = findByMessageRoomId(messageRoomId);
        Member member = memberService.findByMemberId(memberId);

        validateMessageRoomMember(messageRoom, member);

        Long receiverId = getOpponentId(messageRoom, memberId);

        List<MessageSummary> messages = messageRepository
                .findAllByMessageRoomOrderByCreatedAtAsc(messageRoom)
                .stream()
                .map(message -> MessageSummary.from(message, memberId))
                .toList();

        return new MessageListResponse(
                messageRoom.getMessageRoomId(),
                receiverId,
                messages
        );
    }

    private MessageRoom findByMessageRoomId(Long messageRoomId) {
        return messageRoomRepository.findById(messageRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));
    }

    private void validateMessageRoomMember(MessageRoom messageRoom, Member member) {
        Long memberId = member.getMemberId();

        boolean isSender = messageRoom.getSender().getMemberId().equals(memberId);
        boolean isReceiver = messageRoom.getReceiver().getMemberId().equals(memberId);

        if (!isSender && !isReceiver) {
            throw new CustomException(ErrorCode.MESSAGE_ROOM_ACCESS_DENIED);
        }
    }

    private Long getOpponentId(MessageRoom messageRoom, Long memberId) {
        if (messageRoom.getSender().getMemberId().equals(memberId)) {
            return messageRoom.getReceiver().getMemberId();
        }

        return messageRoom.getSender().getMemberId();
    }
}