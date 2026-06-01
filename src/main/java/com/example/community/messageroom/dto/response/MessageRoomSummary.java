package com.example.community.messageroom.dto.response;

import com.example.community.message.domain.Message;
import com.example.community.messageroom.domain.MessageRoom;
import com.example.community.post.domain.Post;

import java.time.LocalDateTime;

public record MessageRoomSummary(
        Long messageRoomId,
        String displayName,
        String lastMessage,
        LocalDateTime lastSentAt,
        MessageRoomPostSummary post
) {
    public static MessageRoomSummary from(MessageRoom messageRoom, Message latestMessage, Long memberId) {
        Post post = messageRoom.getPost();
        MessageRoomPostSummary postSummary = post != null
                ? new MessageRoomPostSummary(post.getPostId(), post.getTitle())
                : null;

        return new MessageRoomSummary(
                messageRoom.getMessageRoomId(),
                messageRoom.getOpponent(memberId).getNickname(),
                latestMessage.getContent(),
                latestMessage.getCreatedAt(),
                postSummary
        );
    }
}
