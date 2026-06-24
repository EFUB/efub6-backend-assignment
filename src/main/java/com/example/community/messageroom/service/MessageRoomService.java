package com.example.community.messageroom.service;

import com.example.community.global.exception.CustomException;
import com.example.community.global.exception.ErrorCode;
import com.example.community.member.domain.Member;
import com.example.community.member.service.MemberService;
import com.example.community.message.domain.Message;
import com.example.community.message.repository.MessageRepository;
import com.example.community.messageroom.domain.MessageRoom;
import com.example.community.messageroom.dto.request.CreateMessageRoomRequest;
import com.example.community.messageroom.dto.response.CreateMessageRoomResponse;
import com.example.community.messageroom.dto.response.MessageRoomCheckResponse;
import com.example.community.messageroom.dto.response.MessageRoomListResponse;
import com.example.community.messageroom.dto.response.MessageRoomSummary;
import com.example.community.messageroom.repository.MessageRoomRepository;
import com.example.community.notification.service.NotificationService;
import com.example.community.post.domain.Post;
import com.example.community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageRoomService {

    private final MessageRoomRepository messageRoomRepository;
    private final MessageRepository messageRepository;
    private final MemberService memberService;
    private final PostService postService;
    private final NotificationService notificationService;

    // 쪽지방 생성
    @Transactional
    public CreateMessageRoomResponse createMessageRoom(Long senderId, CreateMessageRoomRequest request) {
        // 자기 자신에게 쪽지 불가
        if (senderId.equals(request.receiverId())) {
            throw new CustomException(ErrorCode.MESSAGE_ROOM_SELF_SEND);
        }

        Member sender = memberService.findByMemberId(senderId);
        Member receiver = memberService.findByMemberId(request.receiverId());

        // 동일한 쪽지방이 이미 존재하면 중복 생성 방지
        if (request.postId() != null) {
            // 특정 게시글 기반 쪽지방 중복 확인
            messageRoomRepository.findByPostAndParticipants(request.postId(), senderId, request.receiverId())
                    .ifPresent(room -> { throw new CustomException(ErrorCode.MESSAGE_ROOM_ALREADY_EXISTS); });
        } else {
            // 게시글 없는 다이렉트 쪽지방 중복 확인
            messageRoomRepository.findDirectRoomByParticipants(senderId, request.receiverId())
                    .ifPresent(room -> { throw new CustomException(ErrorCode.MESSAGE_ROOM_ALREADY_EXISTS); });
        }

        // postId가 있으면 게시글 연결, 없으면 다이렉트 쪽지방
        Post post = request.postId() != null ? postService.findByPostId(request.postId()) : null;

        MessageRoom messageRoom = MessageRoom.builder()
                .post(post)
                .sender(sender)
                .receiver(receiver)
                .build();
        MessageRoom savedRoom = messageRoomRepository.save(messageRoom);
        notificationService.createMessageRoomNotification(receiver);

        // 쪽지방 생성과 동시에 첫 번째 메시지 저장
        Message message = Message.builder()
                .messageRoom(savedRoom)
                .sender(sender)
                .content(request.content())
                .build();
        Message savedMessage = messageRepository.save(message);

        return CreateMessageRoomResponse.from(savedRoom, savedMessage);
    }

    // 두 회원 간 쪽지방 존재 여부 조회 (postId 없으면 direct room 조회)
    @Transactional(readOnly = true)
    public MessageRoomCheckResponse checkMessageRoom(Long memberId, Long receiverId, Long postId) {
        Optional<MessageRoom> messageRoom = postId != null
                ? messageRoomRepository.findByPostAndParticipants(postId, memberId, receiverId)
                : messageRoomRepository.findDirectRoomByParticipants(memberId, receiverId);

        return new MessageRoomCheckResponse(
                messageRoom.orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_ROOM_NOT_FOUND)).getMessageRoomId()
        );
    }

    // 쪽지방 목록 조회
    @Transactional(readOnly = true)
    public MessageRoomListResponse getMessageRooms(Long memberId) {
        memberService.findByMemberId(memberId);

        // 내가 참여한 모든 쪽지방 조회
        List<MessageRoom> rooms = messageRoomRepository.findAllByMemberIdWithDetails(memberId);

        if (rooms.isEmpty()) {
            return new MessageRoomListResponse(List.of());
        }

        List<Long> roomIds = rooms.stream()
                .map(MessageRoom::getMessageRoomId)
                .toList();

        // N개 방의 최신 메시지를 한 번에 조회 (N+1 방지)
        Map<Long, Message> latestMessageMap = messageRepository.findLatestMessagesForRooms(roomIds)
                .stream()
                // roomId를 key, 최신 메시지를 value로 Map 구성
                .collect(Collectors.toMap(
                        m -> m.getMessageRoom().getMessageRoomId(),
                        m -> m,
                        (a, b) -> a // 동일 createdAt 충돌 시 첫 번째 유지
                ));

        List<MessageRoomSummary> summaries = rooms.stream()
                .map(room -> {
                    Message latestMessage = latestMessageMap.get(room.getMessageRoomId());
                    if (latestMessage == null) {
                        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
                    }
                    return MessageRoomSummary.from(room, latestMessage, memberId);
                })
                .sorted(Comparator.comparing(MessageRoomSummary::lastSentAt).reversed())
                .toList();

        return new MessageRoomListResponse(summaries);
    }

    // 쪽지방 삭제
    @Transactional
    public void deleteMessageRoom(Long messageRoomId, Long memberId) {
        MessageRoom messageRoom = messageRoomRepository.findById(messageRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));

        validateParticipant(messageRoom, memberId);
        messageRoomRepository.delete(messageRoom);
    }

    // 쪽지방 참여자 여부 검증
    private void validateParticipant(MessageRoom messageRoom, Long memberId) {
        if (messageRoom.isNotParticipant(memberId)) {
            throw new CustomException(ErrorCode.MESSAGE_ROOM_ACCOUNT_MISMATCH);
        }
    }
}
