package efub.assignment.community.messageRoom.service;

import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.repository.MessageRepository;
import efub.assignment.community.message.service.MessageService;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly= true)
public class MessageRoomService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final MessageRoomRepository messageRoomRepository;
    private final MessageService messageService;
    private final NotificationService notificationService;
    private final MessageRepository messageRepository;

    // 쪽지방 생성
    @Transactional
    public CreateMessageRoomResponseDto createMessageRoom(Long creatorId, CreateMessageRoomRequestDto requestDto) {
        if (creatorId.equals(requestDto.receiverId())) {
            throw new CustomException(ErrorCode.MESSAGE_ROOM_SELF_NOT_ALLOWED);
        }

        Member creator = memberRepository.findById(creatorId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Member participant = memberRepository.findById(requestDto.receiverId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(requestDto.postId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 이미 존재하는 쪽지방 체크
        messageRoomRepository.findByParticipantsAndPost(creator,participant,post)
                .ifPresent(messageRoom -> {
                    throw new CustomException(ErrorCode.MESSAGE_ROOM_ALREADY_EXISTS);
                });

        // 쪽지방 생성
        MessageRoom messageRoom = MessageRoom.builder()
                .creator(creator)
                .participant(participant)
                .post(post)
                .build();

        MessageRoom savedRoom = messageRoomRepository.save(messageRoom);

        // 첫 메시지 저장
        messageService.createFirstMessage(savedRoom, creator, requestDto.content());

        // 알림 생성
        notificationService.createMessageRoomNotification(participant);

        return CreateMessageRoomResponseDto.of(savedRoom, requestDto.content());
    }

    public MessageRoomListResponseDto getMessageRoomByMember(
            Long authId,
            Long memberId
    ) {
        if (!authId.equals(memberId)) {
            throw new CustomException(ErrorCode.MESSAGE_ROOM_ACCESS_DENIED);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<MessageRoomSummaryDto> messageRooms = messageRoomRepository
                .findAllByCreatorOrParticipant(member, member)
                .stream()
                .map(messageRoom -> {
                    Optional<Message> latestMessage =
                            messageRepository.findTopByMessageRoomOrderByCreatedAtDesc(messageRoom);

                    String latestContent = latestMessage
                            .map(Message::getContent)
                            .orElse(null);

                    LocalDateTime latestCreatedAt = latestMessage
                            .map(Message::getCreatedAt)
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

    public MessageRoomCheckResponseDto getMessageRoom(Long senderId, Long receiverId, Long postId) {
        Member creator = memberRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Member participant = memberRepository.findById(receiverId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        MessageRoom messageRoom = messageRoomRepository.findByParticipantsAndPost(creator, participant, post)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));

        return new MessageRoomCheckResponseDto(messageRoom.getMessageRoomId());
    }


    @Transactional
    public void deleteMessageRoom(Long messageRoomId, Long memberId) {
        MessageRoom messageRoom = findByMessageRoomId(messageRoomId);

        if (!messageRoom.isParticipant(memberId)) {
            throw new CustomException(ErrorCode.MESSAGE_ROOM_ACCESS_DENIED);
        }

        messageRepository.deleteAllByMessageRoom(messageRoom);
        messageRoomRepository.delete(messageRoom);
    }

    public MessageRoom findByMessageRoomId(Long messageRoomId) {
        return messageRoomRepository.findById(messageRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));
    }
}
