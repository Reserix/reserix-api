package com.reserix.api.chat.controller;

import com.reserix.api.chat.dto.ChatMessageListResponse;
import com.reserix.api.chat.dto.ChatMessageRequest;
import com.reserix.api.chat.dto.ChatMessageResponse;
import com.reserix.api.chat.dto.ChatSessionCloseResponse;
import com.reserix.api.chat.dto.ChatSessionCreateResponse;
import com.reserix.api.chat.service.ChatCurrentUserResolver;
import com.reserix.api.chat.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;
    private final ChatCurrentUserResolver currentUserResolver;

    public ChatController(ChatService chatService, ChatCurrentUserResolver currentUserResolver) {
        this.chatService = chatService;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping("/sessions")
    public ChatSessionCreateResponse createSession(Authentication authentication) {
        Long userId = currentUserResolver.resolveUserId(authentication);
        return chatService.createSession(userId);
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public ChatMessageResponse sendMessage(
            @PathVariable Long sessionId,
            @Valid @RequestBody ChatMessageRequest request,
            Authentication authentication
    ) {
        Long userId = currentUserResolver.resolveUserId(authentication);
        return chatService.sendMessage(sessionId, userId, request.message());
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ChatMessageListResponse getMessages(
            @PathVariable Long sessionId,
            Authentication authentication
    ) {
        Long userId = currentUserResolver.resolveUserId(authentication);
        return chatService.getMessages(sessionId, userId);
    }

    @PostMapping("/sessions/{sessionId}/close")
    public ChatSessionCloseResponse closeSession(
            @PathVariable Long sessionId,
            Authentication authentication
    ) {
        Long userId = currentUserResolver.resolveUserId(authentication);
        return chatService.closeSession(sessionId, userId);
    }
}
