package com.reserix.api.chat.entity;

import com.reserix.api.chat.enums.ChatSessionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_sessions")
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Keep this as Long instead of @ManyToOne User.
     * Reason: chat module should not depend on the exact User entity package/name.
     * The database FK still protects referential integrity.
     */
    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ChatSessionStatus status = ChatSessionStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ChatSession() {
    }

    public ChatSession(Long userId) {
        this.userId = userId;
        this.status = ChatSessionStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (this.status == null) {
            this.status = ChatSessionStatus.ACTIVE;
        }

        if (this.createdAt == null) {
            this.createdAt = now;
        }

        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void close() {
        this.status = ChatSessionStatus.CLOSED;
    }

    public void expire() {
        this.status = ChatSessionStatus.EXPIRED;
    }

    public boolean isActive() {
        return this.status == ChatSessionStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public ChatSessionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setStatus(ChatSessionStatus status) {
        this.status = status;
    }
}
