package com.reserix.api.chat.repository;

import com.reserix.api.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySession_IdOrderByCreatedAtAsc(Long sessionId);
}
