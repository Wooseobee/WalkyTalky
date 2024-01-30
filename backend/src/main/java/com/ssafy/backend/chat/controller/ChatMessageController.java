package com.ssafy.backend.chat.controller;

import com.ssafy.backend.chat.domain.ChatMessage;
import com.ssafy.backend.chat.service.ChatMessageService;
import com.ssafy.backend.chat.service.ChatService;
import com.ssafy.backend.chat.service.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final RedisPublisher redisPublisher;
    private final ChatService chatService;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/message")
    public void message(ChatMessage chatMessage) {
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatService.enterChatRoom(chatMessage.getChatSeq());
            chatMessage.setContent("@@님이 입장하셨습니다.");
        }
        redisPublisher.publish(chatService.getTopic(chatMessage.getChatSeq()), chatMessage);
        chatMessageService.saveMessage(chatMessage);
    }
}
