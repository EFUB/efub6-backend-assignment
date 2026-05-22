package efub.assignment.community.message.service;

import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.domain.MessageRoom;
import efub.assignment.community.message.dto.request.MessageRoomCreateRequest;
import efub.assignment.community.message.dto.response.MessageRoomCreateResponse;
import efub.assignment.community.message.dto.response.MessageRoomExistResponse;
import efub.assignment.community.message.dto.response.MessageRoomListResponse;
import efub.assignment.community.message.dto.summary.MessageRoomSummary;
import efub.assignment.community.message.repository.MessageRepository;
import efub.assignment.community.message.repository.MessageRoomRepository;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageRoomService {

    private final MemberService memberService;
    private final PostService postService;
    private final MessageRoomRepository messageRoomRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public MessageRoomCreateResponse createMessageRoom(
            Long postId,
            Long senderId,
            MessageRoomCreateRequest request
    ) {
        Member sender = memberService.findByMemberId(senderId);
        Post post = postService.findByPostId(postId);
        Member receiver = post.getAuthor();

        if (sender.getMemberId().equals(receiver.getMemberId())) {
            throw new CustomException(ErrorCode.MESSAGE_TO_SELF_NOT_ALLOWED);
        }

        if (messageRoomRepository.existsBySenderAndReceiverAndPost(sender, receiver, post)) {
            throw new CustomException(ErrorCode.MESSAGE_ROOM_ALREADY_EXISTS);
        }

        MessageRoom messageRoom = MessageRoom.builder()
                .sender(sender)
                .receiver(receiver)
                .post(post)
                .build();

        MessageRoom savedMessageRoom = messageRoomRepository.save(messageRoom);

        Message message = Message.builder()
                .messageRoom(savedMessageRoom)
                .sender(sender)
                .content(request.getContent())
                .build();

        Message savedMessage = messageRepository.save(message);

        return MessageRoomCreateResponse.from(savedMessageRoom, savedMessage);
    }

    @Transactional(readOnly = true)
    public Optional<MessageRoomExistResponse> existMessageRoom(
            Long memberId,
            Long postId
    ) {
        Member sender = memberService.findByMemberId(memberId);
        Post post = postService.findByPostId(postId);
        Member receiver = post.getAuthor();

        return messageRoomRepository
                .findBySenderAndReceiverAndPost(sender, receiver, post)
                .map(MessageRoomExistResponse::from);
    }

    @Transactional(readOnly = true)
    public MessageRoomListResponse getMessageRooms(Long memberId) {
        Member member = memberService.findByMemberId(memberId);

        List<MessageRoomSummary> messageRooms = messageRoomRepository
                .findAllBySenderOrReceiver(member, member)
                .stream()
                .map(messageRoom -> {
                    Message lastMessage = messageRepository
                            .findTopByMessageRoomOrderByCreatedAtDesc(messageRoom)
                            .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_NOT_FOUND));

                    return MessageRoomSummary.from(messageRoom, lastMessage);
                })
                .sorted((room1, room2) -> room2.getLastSentAt().compareTo(room1.getLastSentAt()))
                .toList();

        return new MessageRoomListResponse(messageRooms);
    }

}