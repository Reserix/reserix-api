package com.reserix.api.chat.service;

import com.reserix.api.chat.enums.ChatIntent;
import com.reserix.api.chat.enums.ChatToolResultStatus;
import com.reserix.api.chat.tool.ChatToolResult;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ChatDraftResponder {

    /*
     * Temporary deterministic responses.
     *
     * This class exists only so the API can be tested before LLM/tool integration.
     * Replace these responses later with LLM + backend tool results.
     */
    public String createResponse(ChatIntent intent, String userMessage, Optional<ChatToolResult> toolResult) {
        if (toolResult.isPresent()) {
            ChatToolResult result = toolResult.get();
            if (result.status() == ChatToolResultStatus.SUCCESS ||
                    result.status() == ChatToolResultStatus.EMPTY ||
                    result.status() == ChatToolResultStatus.AUTH_REQUIRED ||
                    result.status() == ChatToolResultStatus.VALIDATION_FAILED ||
                    result.status() == ChatToolResultStatus.ERROR) {
                return result.userSafeSummary();
            }
        }
        return switch (intent) {
            case GREETING -> "Hello, I am Reserix AI Assistant. I can help with movie search, screening times, seat availability, reservation lookup, and payment status checks.";
            case RESERVATION_HELP -> "I can help you make a reservation, but I must check the screening and seat availability first. I will ask for confirmation before creating any pending reservation.";
            case RESERVATION_CANCEL_REQUEST -> "I can help cancel a reservation, but I will ask for explicit confirmation before cancellation.";
            case SCREENING_SEARCH -> "Which movie’s screening times would you like to check?";
            case UNKNOWN -> "Sorry, I can’t handle that request yet. I can help with movie search, screening times, seat availability, reservation lookup, and payment status checks.";
            default -> "I can help with that, but the required tool is not connected yet.";
        };
    }
}
