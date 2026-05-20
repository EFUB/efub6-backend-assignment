package efub.assignment.community.message.service;

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
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public MessageRoomResponse createMessageRoom(Long senderId, CreateMessageRoomRequest request) {
        Member sender = findByMemberId(senderId);
        Member receiver = findByMemberId(request.getReceiverId());
        Post post = findByPostId(request.getPostId());

        // 내가 나 자신과 쪽지방을 만들 순 없음
        if(senderId.equals(request.getReceiverId())) {
            throw new CustomException(ErrorCode.CANNOT_MESSAGE_SELF);
        }

        // 이미 쪽지방이 있는 경우 또 만들면 안됨
        messageRoomRepository.findExistingMessageRoom(post, sender, receiver)
                .ifPresent(room -> {
                    throw new CustomException(ErrorCode.MESSAGE_ROOM_ALREADY_EXISTS);
                });

        MessageRoom newMessageRoom = request.toEntity(sender, receiver, post);
        messageRoomRepository.save(newMessageRoom);

        Message firstMessage = Message.builder()
                .sender(sender)
                .messageRoom(newMessageRoom)
                .content(request.getFirstMessage())
                .build();
        messageRepository.save(firstMessage);

        newMessageRoom.getMessages().add(firstMessage);

        return MessageRoomResponse.from(newMessageRoom);
    }

    @Transactional(readOnly = true)
    public Long checkMessageRoomExists(Long senderId, Long receiverId, Long postId) {
        Post post = findByPostId(postId);
        Member sender = findByMemberId(senderId);
        Member receiver = findByMemberId(receiverId);

        // sender - receiver의 쪽지방은 순서 상관없이 1개만 존재.
        MessageRoom messageRoom = messageRoomRepository.findExistingMessageRoom(post, sender, receiver)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_ROOM_NOT_FOUND));

        return messageRoom.getId();
    }

    @Transactional(readOnly = true)
    public MessageRoomListResponse getAllMessageRooms(Long memberId) {
        Member member = findByMemberId(memberId);

        List<MessageRoomSummary> messageRoomSummaries = messageRoomRepository.findAllBySenderOrReceiver(member)
                .stream()
                .map(MessageRoomSummary::from)
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
        if (!messageRoom.getSender().equals(member) && !messageRoom.getReceiver().equals(member)) {
            throw new CustomException(ErrorCode.NOT_MESSAGE_ROOM_PARTICIPANT);
        }
    }



}
