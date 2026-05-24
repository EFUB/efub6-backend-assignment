package efub.assignment.community.message.service;

import efub.assignment.community.comment.domain.Comment;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.domain.MessageRoom;
import efub.assignment.community.message.dto.request.CreateMessageRoomRequest;
import efub.assignment.community.message.dto.response.MessageRoomListResponse;
import efub.assignment.community.message.dto.response.MessageRoomResponse;
import efub.assignment.community.message.dto.summary.MessageRoomSummary;
import efub.assignment.community.message.repository.MessageRepository;
import efub.assignment.community.message.repository.MessageRoomRepository;
import efub.assignment.community.notification.domain.Notification;
import efub.assignment.community.notification.domain.NotificationType;
import efub.assignment.community.notification.repository.NotificationRepository;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.repository.PostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageRoomService {
    private final MessageRoomRepository messageRoomRepository;
    private final MessageRepository messageRepository;
    private final MessageService messageService;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public MessageRoomResponse createMessageRoom(Long creatorId, CreateMessageRoomRequest request) {
        // 내가 나 자신과 쪽지방을 만들 순 없음
        if(creatorId.equals(request.getPartnerId())) {
            throw new CustomException(ErrorCode.CANNOT_MESSAGE_SELF);
        }

        Member creator = findByMemberId(creatorId);
        Member partner = findByMemberId(request.getPartnerId());
        Post post = findByPostId(request.getPostId());

        // 이미 쪽지방이 있는 경우 또 만들면 안됨
        messageRoomRepository.findExistingMessageRoom(post, creator, partner)
                .ifPresent(room -> {
                    throw new CustomException(ErrorCode.MESSAGE_ROOM_ALREADY_EXISTS);
                });

        MessageRoom newMessageRoom = request.toEntity(creator, partner, post);
        messageRoomRepository.save(newMessageRoom);

        Message firstMessage = messageService.createFirstMessage(newMessageRoom, creator, request.getFirstMessage());
        newMessageRoom.getMessages().add(firstMessage);

        String notificationContent = NotificationType.NEW_MESSAGE_ROOM_CREATED.getMessagePrefix();
        Notification notification = Notification.builder()
                .receiver(partner)
                .type(NotificationType.NEW_MESSAGE_ROOM_CREATED)
                .content(notificationContent)
                .build();
        notificationRepository.save(notification);

        return MessageRoomResponse.from(newMessageRoom);
    }

    @Transactional(readOnly = true)
    public Long checkMessageRoomExists(Long creatorId, Long partnerId, Long postId) {
        Post post = findByPostId(postId);
        Member creator = findByMemberId(creatorId);
        Member partner = findByMemberId(partnerId);

        // sender - receiver의 쪽지방은 순서 상관없이 1개만 존재.
        return messageRoomRepository.findExistingMessageRoom(post, creator, partner)
                .map(MessageRoom::getId)
                .orElse(null);

    }

    @Transactional(readOnly = true)
    public MessageRoomListResponse getAllMessageRooms(Long memberId) {
        Member member = findByMemberId(memberId);

        List<MessageRoomSummary> messageRoomSummaries = messageRoomRepository.findAllBySenderOrReceiverWithFetchJoin(member)
                .stream()
                .map(room -> {
                    List<Message> messages = messageRepository.findTop1ByMessageRoomWithFetchJoin(room);
                    if (messages.isEmpty()) {
                        throw new CustomException(ErrorCode.MESSAGE_NOT_FOUND);
                    }
                    return MessageRoomSummary.of(room, messages.get(0));
                })
                .toList();

        return new MessageRoomListResponse(messageRoomSummaries);
    }

    @Transactional
    public void deleteMessageRoom(Long messageRoomId, Long memberId) {
        MessageRoom messageRoom = messageRoomRepository.findById(messageRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));
        Member member = findByMemberId(memberId);

        authorizeMessageRoomParticipant(messageRoom, member);
        messageRoomRepository.delete(messageRoom);
    }



    private Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private Post findByPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private void authorizeMessageRoomParticipant(MessageRoom messageRoom, Member member) {
        if (!messageRoom.isParticipant(member)) {
            throw new CustomException(ErrorCode.NOT_MESSAGE_ROOM_PARTICIPANT);
        }
    }
}
