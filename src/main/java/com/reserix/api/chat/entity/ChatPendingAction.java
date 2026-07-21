package com.reserix.api.chat.entity;

import com.reserix.api.chat.enums.ChatPendingActionStatus;
import com.reserix.api.chat.enums.ChatPendingActionType;
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
@Table(name = "chat_pending_actions")
public class ChatPendingAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    /*
     * Keep as Long to avoid coupling this chat module to a concrete User entity.
     * Always set this value from the authenticated security principal, not from user text.
     */
    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 80)
    private ChatPendingActionType actionType;

    @Column(name = "action_payload_json", nullable = false, columnDefinition = "TEXT")
    private String actionPayloadJson;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ChatPendingActionStatus status = ChatPendingActionStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "consumed_at")
    private LocalDateTime consumedAt;

    protected ChatPendingAction() {
    }

    public ChatPendingAction(
            ChatSession session,
            Long userId,
            ChatPendingActionType actionType,
            String actionPayloadJson,
            String summary,
            LocalDateTime expiresAt
    ) {
        this.session = session;
        this.userId = userId;
        this.actionType = actionType;
        this.actionPayloadJson = actionPayloadJson;
        this.summary = summary;
        this.expiresAt = expiresAt;
        this.status = ChatPendingActionStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = ChatPendingActionStatus.PENDING;
        }

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public boolean isPending() {
        return this.status == ChatPendingActionStatus.PENDING;
    }

    public boolean isExpired(LocalDateTime now) {
        return this.expiresAt != null && !this.expiresAt.isAfter(now);
    }

    public void consume() {
        this.status = ChatPendingActionStatus.CONSUMED;
        this.consumedAt = LocalDateTime.now();
    }

    public void expire() {
        this.status = ChatPendingActionStatus.EXPIRED;
    }

    public void cancel() {
        this.status = ChatPendingActionStatus.CANCELED;
    }

    public Long getId() {
        return id;
    }

    public ChatSession getSession() {
        return session;
    }

    public Long getUserId() {
        return userId;
    }

    public ChatPendingActionType getActionType() {
        return actionType;
    }

    public String getActionPayloadJson() {
        return actionPayloadJson;
    }

    public String getSummary() {
        return summary;
    }

    public ChatPendingActionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getConsumedAt() {
        return consumedAt;
    }

    public void setSession(ChatSession session) {
        this.session = session;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setActionType(ChatPendingActionType actionType) {
        this.actionType = actionType;
    }

    public void setActionPayloadJson(String actionPayloadJson) {
        this.actionPayloadJson = actionPayloadJson;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setStatus(ChatPendingActionStatus status) {
        this.status = status;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setConsumedAt(LocalDateTime consumedAt) {
        this.consumedAt = consumedAt;
    }
}
