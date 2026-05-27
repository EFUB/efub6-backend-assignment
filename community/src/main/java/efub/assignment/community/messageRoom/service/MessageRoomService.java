package efub.assignment.community.messageRoom.service;

import efub.assignment.community.alarm.service.AlarmService;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.repository.MessageRepository;
import efub.assignment.community.messageRoom.domain.MessageRoom;
import efub.assignment.community.messageRoom.dto.request.CreateMessageRoomRequest;
import efub.assignment.community.messageRoom.dto.response.CreateMessageRoomResponse;
import efub.assignment.community.messageRoom.dto.response.GetMessageRoomResponse;
import efub.assignment.community.messageRoom.dto.response.MessageRoomResponse;
import efub.assignment.community.messageRoom.repository.MessageRoomRepository;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageRoomService {

    private final MemberService memberService;
    private final PostService postService;
    private final MessageRoomRepository messageRoomRepository;
    private final MessageRepository messageRepository;
    private final AlarmService alarmService;

    // 쪽지방 생성
    @Transactional
    public CreateMessageRoomResponse createMessageRoom(Long senderId, CreateMessageRoomRequest request) {
        Post post = postService.findByPostId(request.getPostId());
        Member sender = memberService.findByMemberId(senderId);
        Member receiver = memberService.findByMemberId(request.getReceiverId());

        MessageRoom newMessageRoom = request.toEntity(sender, receiver, post);

        Message firstMessage = new Message(newMessageRoom, sender, request.getMessage());
        newMessageRoom.getMessages().add(firstMessage);

        messageRoomRepository.save(newMessageRoom);

        alarmService.createMessageRoomAlarm(receiver);

        return CreateMessageRoomResponse.from(newMessageRoom, firstMessage);
    }

    // 쪽지방 여부 확인
    public GetMessageRoomResponse getExistMessageRoom(Long senderId, Long receiverId, Long postId) {
        Post post = postService.findByPostId(postId);
        Member sender = memberService.findByMemberId(senderId);
        Member receiver = memberService.findByMemberId(receiverId);

        Optional<MessageRoom> messageRoom = messageRoomRepository.findBySenderAndReceiverAndPost(sender, receiver, post);

        return messageRoom.map(GetMessageRoomResponse::from)
                .orElse(GetMessageRoomResponse.empty());
    }

    // 쪽지방 삭제
    @Transactional
    public void deleteMessageRoom(Long messageRoomId, Long requesterId) {
        MessageRoom messageRoom = findByMessageRoomId(messageRoomId);
        Member requester = memberService.findByMemberId(requesterId);

        authorizeMessageRoomMember(messageRoom, requester);

        messageRoomRepository.delete(messageRoom);
    }

    // --------------- helper function -------------- //
    public MessageRoom findByMessageRoomId(Long messageRoomId) {
        return messageRoomRepository.findById(messageRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGEROOM_NOT_FOUND));
    }

    // 쪽지방 목록 조회
    public List<MessageRoomResponse> getMessageRoomList(Long requesterId) {
        Member requester = memberService.findByMemberId(requesterId);
        List<MessageRoom> messageRooms = messageRoomRepository.findBySenderOrReceiver(requester, requester);

        return messageRooms.stream()
                .map(messageRoom -> {
                    Message latestMessage = messageRepository
                            .findTopByMessageRoomOrderByCreatedAtDesc(messageRoom)
                            .orElse(null);
                    return MessageRoomResponse.from(messageRoom, latestMessage);
                })
                .collect(Collectors.toList());
    }

    public void authorizeMessageRoomMember(MessageRoom messageRoom, Member sender) {
        if (!messageRoom.getSender().equals(sender) && !messageRoom.getReceiver().equals(sender)) {
            throw new CustomException(ErrorCode.NOT_MESSAGEROOM_MEMBER);
        }
    }
}
