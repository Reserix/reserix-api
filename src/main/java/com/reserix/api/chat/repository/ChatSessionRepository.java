package com.reserix.api.chat.repository;

import com.reserix.api.chat.entity.ChatSession;
import com.reserix.api.chat.enums.ChatSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    List<ChatSession> findByUserIdAndStatusOrderByUpdatedAtDesc(Long userId, ChatSessionStatus status);
}
