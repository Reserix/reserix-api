package com.reserix.api.chat.entity;

import com.reserix.api.chat.enums.ChatIntent;
import com.reserix.api.chat.enums.ChatRole;
import com.reserix.api.chat.enums.ChatToolName;
import com.reserix.api.chat.enums.ChatToolResultStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private ChatRole role;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "intent", length = 60)
    private ChatIntent intent;

    @Enumerated(EnumType.STRING)
    @Column(name = "tool_name", length = 80)
    private ChatToolName toolName;

    @Enumerated(EnumType.STRING)
    @Column(name = "tool_status", length = 40)
    private ChatToolResultStatus toolStatus;

    @Column(name = "tool_input_json", columnDefinition = "TEXT")
    private String toolInputJson;

    @Column(name = "tool_result_json", columnDefinition = "TEXT")
    private String toolResultJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected ChatMessage() {
    }

    private ChatMessage(
            ChatSession session,
            ChatRole role,
            String content,
            ChatIntent intent,
            ChatToolName toolName,
            ChatToolResultStatus toolStatus,
            String toolInputJson,
            String toolResultJson
    ) {
        this.session = session;
        this.role = role;
        this.content = content;
        this.intent = intent;
        this.toolName = toolName;
        this.toolStatus = toolStatus;
        this.toolInputJson = toolInputJson;
        this.toolResultJson = toolResultJson;
    }

    public static ChatMessage user(ChatSession session, String content) {
        return new ChatMessage(session, ChatRole.USER, content, null, null, null, null, null);
    }

    public static ChatMessage assistant(ChatSession session, String content, ChatIntent intent) {
        return new ChatMessage(session, ChatRole.ASSISTANT, content, intent, null, null, null, null);
    }

    public static ChatMessage system(ChatSession session, String content) {
        return new ChatMessage(session, ChatRole.SYSTEM, content, null, null, null, null, null);
    }

    public static ChatMessage tool(
            ChatSession session,
            ChatToolName toolName,
            ChatToolResultStatus toolStatus,
            String toolInputJson,
            String toolResultJson
    ) {
        String content = toolResultJson == null ? "" : toolResultJson;
        return new ChatMessage(
                session,
                ChatRole.TOOL,
                content,
                null,
                toolName,
                toolStatus,
                toolInputJson,
                toolResultJson
        );
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public boolean isUserMessage() {
        return this.role == ChatRole.USER;
    }

    public boolean isAssistantMessage() {
        return this.role == ChatRole.ASSISTANT;
    }

    public boolean isToolMessage() {
        return this.role == ChatRole.TOOL;
    }

    public Long getId() {
        return id;
    }

    public ChatSession getSession() {
        return session;
    }

    public ChatRole getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public ChatIntent getIntent() {
        return intent;
    }

    public ChatToolName getToolName() {
        return toolName;
    }

    public ChatToolResultStatus getToolStatus() {
        return toolStatus;
    }

    public String getToolInputJson() {
        return toolInputJson;
    }

    public String getToolResultJson() {
        return toolResultJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setSession(ChatSession session) {
        this.session = session;
    }

    public void setRole(ChatRole role) {
        this.role = role;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIntent(ChatIntent intent) {
        this.intent = intent;
    }

    public void setToolName(ChatToolName toolName) {
        this.toolName = toolName;
    }

    public void setToolStatus(ChatToolResultStatus toolStatus) {
        this.toolStatus = toolStatus;
    }

    public void setToolInputJson(String toolInputJson) {
        this.toolInputJson = toolInputJson;
    }

    public void setToolResultJson(String toolResultJson) {
        this.toolResultJson = toolResultJson;
    }
}
