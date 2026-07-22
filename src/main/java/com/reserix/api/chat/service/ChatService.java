package com.reserix.api.chat.service;

import com.reserix.api.chat.dto.ChatMessageListResponse;
import com.reserix.api.chat.dto.ChatMessageResponse;
import com.reserix.api.chat.dto.ChatSessionCloseResponse;
import com.reserix.api.chat.dto.ChatSessionCreateResponse;
import com.reserix.api.chat.dto.ChatToolCallDto;
import com.reserix.api.chat.entity.ChatMessage;
import com.reserix.api.chat.entity.ChatSession;
import com.reserix.api.chat.enums.ChatIntent;
import com.reserix.api.chat.repository.ChatMessageRepository;
import com.reserix.api.chat.repository.ChatSessionRepository;
import com.reserix.api.chat.tool.ChatToolContext;
import com.reserix.api.chat.tool.ChatToolExecutor;
import com.reserix.api.chat.tool.ChatToolResult;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatIntentRouter chatIntentRouter;
    private final ChatDraftResponder chatDraftResponder;
    private final ChatToolExecutor chatToolExecutor;

    public ChatService(
			ChatSessionRepository chatSessionRepository, 
			ChatMessageRepository chatMessageRepository, 
			ChatIntentRouter chatIntentRouter, 
			ChatDraftResponder chatDraftResponder, 
			ChatToolExecutor chatToolExecutor) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatIntentRouter = chatIntentRouter;
        this.chatDraftResponder = chatDraftResponder;
        this.chatToolExecutor = chatToolExecutor;
    }

    @Transactional
    public ChatSessionCreateResponse createSession(Long userId) {
        ChatSession session = chatSessionRepository.save(new ChatSession(userId));
        return new ChatSessionCreateResponse(session.getId(), session.getStatus(), session.getCreatedAt());
    }

    @Transactional
    public ChatMessageResponse sendMessage(Long sessionId, Long currentUserId, String message) {
        ChatSession session = getAccessibleSession(sessionId, currentUserId);

        if (!session.isActive())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Chat session is not active.");

        chatMessageRepository.save(ChatMessage.user(session, message));

        ChatIntent intent = chatIntentRouter.detectIntent(message);
        ChatToolContext toolContext = new ChatToolContext(session.getId(), currentUserId, intent, message);
        Optional<ChatToolResult> toolResult = chatToolExecutor.executeForIntent(intent, toolContext);

        toolResult.ifPresent(
                result ->
                        chatMessageRepository.save(
                                ChatMessage.tool(
                                        session,
                                        result.toolName(),
                                        result.status(),
                                        "{}",
                                        toJson(result)))
        );

        String assistantContent = chatDraftResponder.createResponse(intent, message, toolResult);
        ChatMessage assistantMessage = chatMessageRepository.save(ChatMessage.assistant(session, assistantContent, intent));

        return toResponse(session.getId(), assistantMessage, toolResult);
    }

    @Transactional(readOnly = true)
    public ChatMessageListResponse getMessages(Long sessionId, Long currentUserId) {
        ChatSession session = getAccessibleSession(sessionId, currentUserId);

        List<ChatMessageResponse> messages = chatMessageRepository.findBySession_IdOrderByCreatedAtAsc(session.getId())
                .stream()
                .map(message ->
                        toResponse(
                                session.getId(),
                                message,
                                Optional.empty()))
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

        if (session.getUserId() != null && currentUserId != null && !session.getUserId().equals(currentUserId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot access this chat session.");

        return session;
    }

    private ChatMessageResponse toResponse(Long sessionId, ChatMessage message, Optional<ChatToolResult> toolResult) {
        List<ChatToolCallDto> toolCalls = toolResult
                .map(result ->
                        List.of(new ChatToolCallDto(
                                result.toolName().name(),
                                result.status().name())))
                .orElseGet(List::of);

        return new ChatMessageResponse(
                sessionId,
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getIntent(),
                toolCalls,
                message.getCreatedAt());
    }

    private String toJson(Object value) {
        if (value == null) {
            return "{}";
        }

        String text = String.valueOf(value)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");

        return "{\"summary\":\"" + text + "\"}";
    }
}
