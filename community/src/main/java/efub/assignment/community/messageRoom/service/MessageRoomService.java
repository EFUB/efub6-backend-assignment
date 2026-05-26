package efub.assignment.community.messageRoom.service;

import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.repository.MessageRepository;
import efub.assignment.community.messageRoom.domain.MessageRoom;
import efub.assignment.community.messageRoom.dto.request.CreateMessageRoomRequestDto;
import efub.assignment.community.messageRoom.dto.response.CreateMessageRoomResponseDto;
import efub.assignment.community.messageRoom.dto.response.MessageRoomCheckResponseDto;
import efub.assignment.community.messageRoom.dto.response.MessageRoomListResponseDto;
import efub.assignment.community.messageRoom.dto.summary.MessageRoomSummaryDto;
import efub.assignment.community.messageRoom.repository.MessageRoomRepository;
import efub.assignment.community.notification.service.NotificationService;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.repository.PostRepository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly= true)
public class MessageRoomService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final MessageRoomRepository messageRoomRepository;
    private final MessageRepository messageRepository;
    private final NotificationService notificationService;

    // 쪽지방 생성
    @Transactional
    public CreateMessageRoomResponseDto createMessageRoom(CreateMessageRoomRequestDto requestDto) {

        Member sender = memberRepository.findById(requestDto.senderId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Member receiver = memberRepository.findById(requestDto.receiverId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(requestDto.postId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 자기 자신에게 쪽지 불가
        if (sender.getMemberId().equals(receiver.getMemberId())) {
            throw new CustomException(ErrorCode.MESSAGE_ROOM_SELF_NOT_ALLOWED);
        }

        // 이미 존재하는 쪽지방 체크
        messageRoomRepository.findBySenderAndReceiverAndPost(sender, receiver, post)
                .ifPresent(messageRoom -> {
                    throw new CustomException(ErrorCode.MESSAGE_ROOM_ALREADY_EXISTS);
                });

        // 쪽지방 생성
        MessageRoom messageRoom = MessageRoom.builder()
                .sender(sender)
                .receiver(receiver)
                .post(post)
                .build();

        MessageRoom savedRoom = messageRoomRepository.save(messageRoom);

        // 첫 메시지 저장
        Message firstMessage = Message.builder()
                .messageRoom(savedRoom)
                .sender(sender)
                .content(requestDto.content())
                .build();

        messageRepository.save(firstMessage);
        // 알림 생성
        notificationService.createMessageRoomNotification(receiver);

        return CreateMessageRoomResponseDto.of(savedRoom, requestDto.content());
    }

    public MessageRoomListResponseDto getMessageRoomByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<MessageRoomSummaryDto> messageRooms = messageRoomRepository
                .findAllBySenderOrReceiver(member, member)
                .stream()
                .map(messageRoom -> {
                    String latestContent = messageRepository
                            .findTopByMessageRoomOrderByCreatedAtDesc(messageRoom)
                            .map(message -> message.getContent())
                            .orElse(null);

                    LocalDateTime latestCreatedAt = messageRepository
                            .findTopByMessageRoomOrderByCreatedAtDesc(messageRoom)
                            .map(message -> message.getCreatedAt())
                            .orElse(null);

                    return new MessageRoomSummaryDto(
                            messageRoom.getMessageRoomId(),
                            latestContent,
                            latestCreatedAt
                    );
                })
                .toList();

        return new MessageRoomListResponseDto(messageRooms);
    }

    public MessageRoomCheckResponseDto checkMessageRoom(Long senderId, Long receiverId, Long postId) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        MessageRoom messageRoom = messageRoomRepository.findBySenderAndReceiverAndPost(sender, receiver, post)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));

        return new MessageRoomCheckResponseDto(messageRoom.getMessageRoomId());
    }

    @Transactional
    public void deleteMessageRoom(Long messageRoomId) {
        MessageRoom messageRoom = findByMessageRoomId(messageRoomId);
        messageRoomRepository.delete(messageRoom);
    }

    public MessageRoom findByMessageRoomId(Long messageRoomId) {
        return messageRoomRepository.findById(messageRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));
    }
}
