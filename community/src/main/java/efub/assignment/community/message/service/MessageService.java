package efub.assignment.community.message.service;

import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.dto.request.CreateMessageRequest;
import efub.assignment.community.message.dto.response.CreateMessageResponse;
import efub.assignment.community.message.dto.response.GetMessageListResponse;
import efub.assignment.community.message.repository.MessageRepository;
import efub.assignment.community.messageRoom.domain.MessageRoom;
import efub.assignment.community.messageRoom.repository.MessageRoomRepository;
import efub.assignment.community.messageRoom.service.MessageRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MemberService memberService;
    private final MessageRoomService messageRoomService;
    private final MessageRepository messageRepository;

    // 쪽지 생성
    public CreateMessageResponse createMessage(Long messageRoomId, Long senderId, @Valid CreateMessageRequest request) {
        MessageRoom messageRoom = messageRoomService.findByMessageRoomId(messageRoomId);
        Member sender = memberService.findByMemberId(senderId);

        // 쪽지방 참여자인지 확인
        messageRoomService.authorizeMessageRoomMember(messageRoom, sender);

        Message newMessage = new Message(messageRoom, sender, request.getMessage());
        messageRepository.save(newMessage);

        return CreateMessageResponse.from(newMessage);
    }

    // 쪽지방 모든 메시지 조회
    public GetMessageListResponse getMessageList(Long messageRoomId, Long requesterId) {
        MessageRoom messageRoom = messageRoomService.findByMessageRoomId(messageRoomId);
        Member requester = memberService.findByMemberId(requesterId);

        messageRoomService.authorizeMessageRoomMember(messageRoom, requester);

        List<Message> messageList = findAllMessages(messageRoom);

        return GetMessageListResponse.from(messageRoom, messageList, requesterId);
    }

    // ---------------- helper function --------------- //
    // sender가 messageRoom 참여자인지 확인


    // 쪽지방 모든 메시지 반환
    private List<Message> findAllMessages(MessageRoom messageRoom) {
        return messageRepository.findAllByMessageRoom(messageRoom);
    }
}
