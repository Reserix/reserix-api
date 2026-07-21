package com.reserix.api.chat.repository;

import com.reserix.api.chat.entity.ChatPendingAction;
import com.reserix.api.chat.enums.ChatPendingActionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChatPendingActionRepository extends JpaRepository<ChatPendingAction, Long> {

    Optional<ChatPendingAction> findTopBySession_IdAndStatusOrderByCreatedAtDesc(
            Long sessionId,
            ChatPendingActionStatus status
    );

    long deleteByStatusAndExpiresAtBefore(ChatPendingActionStatus status, LocalDateTime expiresAt);
}
