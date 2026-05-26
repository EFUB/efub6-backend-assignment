package efub.assignment.community.message.service;

import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.dto.request.CreateMessageRequestDto;
import efub.assignment.community.message.dto.response.CreateMessageResponseDto;
import efub.assignment.community.message.dto.response.MessageListResponseDto;
import efub.assignment.community.message.dto.summary.MessageSummaryDto;
import efub.assignment.community.message.repository.MessageRepository;
import efub.assignment.community.messageRoom.domain.MessageRoom;
import efub.assignment.community.messageRoom.service.MessageRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageRoomService messageRoomService;
    private final MemberRepository memberRepository;

    // 쪽지 생성
    @Transactional
    public CreateMessageResponseDto createMessage(
            Long messageRoomId,
            Long senderId,
            CreateMessageRequestDto requestDto
    ) {
        MessageRoom messageRoom = messageRoomService.findByMessageRoomId(messageRoomId);

        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        authorizeMessageRoomParticipant(messageRoom, senderId);

        Message message = Message.builder()
                .messageRoom(messageRoom)
                .sender(sender)
                .content(requestDto.content())
                .build();

        Message savedMessage = messageRepository.save(message);

        return CreateMessageResponseDto.from(savedMessage);
    }

    // 특정 쪽지방의 모든 메시지 조회
    public MessageListResponseDto getMessages(Long messageRoomId, Long memberId) {
        MessageRoom messageRoom = messageRoomService.findByMessageRoomId(messageRoomId);

        memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        authorizeMessageRoomParticipant(messageRoom, memberId);

        Long otherMemberId = getOtherMemberId(messageRoom, memberId);

        List<MessageSummaryDto> messages = messageRepository
                .findAllByMessageRoomOrderByCreatedAtAsc(messageRoom)
                .stream()
                .map(message -> MessageSummaryDto.from(message, memberId))
                .toList();

        return new MessageListResponseDto(
                messageRoom.getMessageRoomId(),
                otherMemberId,
                messages
        );
    }

    private void authorizeMessageRoomParticipant(MessageRoom messageRoom, Long memberId) {
        boolean isSender = messageRoom.getSender().getMemberId().equals(memberId);
        boolean isReceiver = messageRoom.getReceiver().getMemberId().equals(memberId);

        if (!isSender && !isReceiver) {
            throw new CustomException(ErrorCode.MESSAGE_ROOM_ACCESS_DENIED);
        }
    }

    private Long getOtherMemberId(MessageRoom messageRoom, Long memberId) {
        if (messageRoom.getSender().getMemberId().equals(memberId)) {
            return messageRoom.getReceiver().getMemberId();
        }
        return messageRoom.getSender().getMemberId();
    }
}