package efub.assignment.community.message.service;

import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.domain.MessageRoom;
import efub.assignment.community.message.dto.request.CreateMessageRequest;
import efub.assignment.community.message.dto.response.MessageListResponse;
import efub.assignment.community.message.dto.response.MessageResponse;
import efub.assignment.community.message.dto.summary.MessageSummary;
import efub.assignment.community.message.repository.MessageRepository;
import efub.assignment.community.message.repository.MessageRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessageRoomRepository messageRoomRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Message createFirstMessage(MessageRoom messageRoom, Member sender, String content) {
        Message firstMessage = Message.builder()
                .sender(sender)
                .messageRoom(messageRoom)
                .content(content)
                .build();

        return messageRepository.save(firstMessage);
    }

    @Transactional
    public MessageResponse createMessage(Long messageRoomId, Long senderId, CreateMessageRequest request) {
        MessageRoom messageRoom = findByMessageRoomId(messageRoomId);
        Member sender = findByMemberId(senderId);

        // 쪽지를 보내는 사람이 그 쪽지방의 일원인지 확인
        authorizeMessageRoomParticipant(messageRoom, sender);

        Message message = request.toEntity(messageRoom, sender);
        messageRepository.save(message);

        return MessageResponse.from(message);
    }

    @Transactional(readOnly = true)
    public MessageListResponse getAllMessages(Long messageRoomId, Long memberId) {
        MessageRoom messageRoom = findByMessageRoomId(messageRoomId);
        Member member = findByMemberId(memberId);

        authorizeMessageRoomParticipant(messageRoom, member);

        return MessageListResponse.of(messageRoom, memberId);
    }

    private Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private MessageRoom findByMessageRoomId(Long messageRoomId) {
        return messageRoomRepository.findById(messageRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));
    }

    private void authorizeMessageRoomParticipant(MessageRoom messageRoom, Member member) {
        if (!messageRoom.getCreator().equals(member) && !messageRoom.getPartner().equals(member)) {
            throw new CustomException(ErrorCode.NOT_MESSAGE_ROOM_PARTICIPANT);
        }
    }
}
