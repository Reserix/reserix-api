package com.reserix.api.chat.service;

import com.reserix.api.chat.dto.ChatMessageListResponse;
import com.reserix.api.chat.dto.ChatMessageResponse;
import com.reserix.api.chat.dto.ChatSessionCloseResponse;
import com.reserix.api.chat.dto.ChatSessionCreateResponse;
import com.reserix.api.chat.entity.ChatMessage;
import com.reserix.api.chat.entity.ChatSession;
import com.reserix.api.chat.enums.ChatIntent;
import com.reserix.api.chat.repository.ChatMessageRepository;
import com.reserix.api.chat.repository.ChatSessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatIntentRouter chatIntentRouter;
    private final ChatDraftResponder chatDraftResponder;

    public ChatService(
            ChatSessionRepository chatSessionRepository,
            ChatMessageRepository chatMessageRepository,
            ChatIntentRouter chatIntentRouter,
            ChatDraftResponder chatDraftResponder
    ) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatIntentRouter = chatIntentRouter;
        this.chatDraftResponder = chatDraftResponder;
    }

    @Transactional
    public ChatSessionCreateResponse createSession(Long userId) {
        ChatSession session = chatSessionRepository.save(new ChatSession(userId));

        return new ChatSessionCreateResponse(
                session.getId(),
                session.getStatus(),
                session.getCreatedAt()
        );
    }

    @Transactional
    public ChatMessageResponse sendMessage(Long sessionId, Long currentUserId, String message) {
        ChatSession session = getAccessibleSession(sessionId, currentUserId);

        if (!session.isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Chat session is not active.");
        }

        chatMessageRepository.save(ChatMessage.user(session, message));

        ChatIntent intent = chatIntentRouter.detectIntent(message);
        String assistantContent = chatDraftResponder.createResponse(intent, message);
        ChatMessage assistantMessage = chatMessageRepository.save(
                ChatMessage.assistant(session, assistantContent, intent)
        );

        return toResponse(session.getId(), assistantMessage);
    }

    @Transactional(readOnly = true)
    public ChatMessageListResponse getMessages(Long sessionId, Long currentUserId) {
        ChatSession session = getAccessibleSession(sessionId, currentUserId);
        List<ChatMessageResponse> messages = chatMessageRepository
                .findBySession_IdOrderByCreatedAtAsc(session.getId())
                .stream()
                .map(message -> toResponse(session.getId(), message))
                .toList();

        return new ChatMessageListResponse(session.getId(), messages);
    }

    @Transactional
    public ChatSessionCloseResponse closeSession(Long sessionId, Long currentUserId) {
        ChatSession session = getAccessibleSession(sessionId, currentUserId);
        session.close();

        return new ChatSessionCloseResponse(session.getId(), session.getStatus());
    }

    private ChatSession getAccessibleSession(Long sessionId, Long currentUserId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat session not found."));

        /*
         * If sessions are created with userId, enforce ownership.
         * If userId is null, it means the current Security principal resolver is not wired yet.
         * Do not use this relaxed rule for reservation/payment tools later.
         */
        if (session.getUserId() != null && currentUserId != null && !session.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot access this chat session.");
        }

        return session;
    }

    private ChatMessageResponse toResponse(Long sessionId, ChatMessage message) {
        return new ChatMessageResponse(
                sessionId,
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getIntent(),
                List.of(),
                message.getCreatedAt()
        );
    }
}
