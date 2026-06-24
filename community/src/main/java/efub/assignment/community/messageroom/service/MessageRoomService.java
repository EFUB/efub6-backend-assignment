package efub.assignment.community.messageroom.service;

import efub.assignment.community.alarm.enums.AlarmType;
import efub.assignment.community.alarm.service.AlarmService;
import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import efub.assignment.community.messageroom.domain.Message;
import efub.assignment.community.messageroom.domain.MessageRoom;
import efub.assignment.community.messageroom.dto.request.MessageRoomRequestDto;
import efub.assignment.community.messageroom.dto.request.CreateMessageRoomDto;
import efub.assignment.community.messageroom.dto.response.CheckMessageRoomDto;
import efub.assignment.community.messageroom.dto.response.MessageRoomDto;
import efub.assignment.community.messageroom.dto.response.MessageRoomListDto;
import efub.assignment.community.messageroom.repository.MessageRoomRepository;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageRoomService {

    private final MemberService memberService;
    private final MessageRoomRepository messageRoomRepository;
    private final PostService postService;
    private final AlarmService alarmService;

    @Transactional
    public MessageRoomDto createMessageRoom(Long requesterId, CreateMessageRoomDto request) {
        Member requester = memberService.findByMemberId(requesterId);
        Member target = memberService.findByMemberId(request.getTargetId());
        Post post = postService.findByPostId(request.getPostId());

        if (messageRoomRepository.existsMessageRoomBetweenMembers(post,requester,target)){
            throw new CustomException(ErrorCode.MESSAGEROOM_ALREADY_EXISTS);
        }

        MessageRoom messageRoom = MessageRoom.builder()
                .post(post)
                .creator(requester)
                .target(target)
                .build();

        Message firstMessage = Message.builder()
                .messageRoom(messageRoom)
                .sender(requester)
                .content(request.getFirstMessageContent())
                .build();
        messageRoom.addMessage(firstMessage);

        messageRoomRepository.save(messageRoom);

        alarmService.createAlarm(AlarmType.MESSAGE_ROOM, target, "쪽지방", "새로운 쪽지방이 생겼어요");

        return MessageRoomDto.of(messageRoom);
    }

    @Transactional(readOnly = true)
    public MessageRoomListDto getMessageRoomList(Long requesterID) {
        Member requester = memberService.findByMemberId(requesterID);
        List<MessageRoom> messageRoomList = messageRoomRepository.findAllByCreatorOrTarget(requester, requester);

        return MessageRoomListDto.of(messageRoomList);
    }

    @Transactional(readOnly = true)
    public CheckMessageRoomDto checkMessageRoomExist(Long requesterId, MessageRoomRequestDto request) {
        Member requester = memberService.findByMemberId(requesterId);
        Member receiver = memberService.findByMemberId(request.getReceiverId());
        Post post = postService.findByPostId(request.getPostId());

        MessageRoom messageRoom = messageRoomRepository.findByPostAndMembers(post, requester, receiver)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGEROOM_NOT_FOUND));

        return CheckMessageRoomDto.of(messageRoom);
    }

    @Transactional
    public void deleteMessageRoom(Long requesterId, MessageRoomRequestDto request) {
        Member requester = memberService.findByMemberId(requesterId);
        Post post = postService.findByPostId(request.getPostId());

        MessageRoom messageRoom = messageRoomRepository.findByPostAndMember(post, requester)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGEROOM_NOT_FOUND));

        authorizeMessageRoomMember(messageRoom, requester);
        messageRoomRepository.delete(messageRoom);
    }

    public void authorizeMessageRoomMember(MessageRoom messageRoom, Member requester) {
        if(!messageRoom.getCreator().equals(requester) && !messageRoom.getTarget().equals(requester)){
            throw new CustomException(ErrorCode.MESSAGEROOM_MEMBER_MISMATCH);
        }
    }

    public MessageRoom findByMessageRoomId (Long messageRoomId) {
        return messageRoomRepository.findById(messageRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGEROOM_NOT_FOUND));
    }
}
