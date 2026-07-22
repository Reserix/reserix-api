package com.reserix.api.chat.tool;

import com.reserix.api.chat.enums.ChatIntent;
import com.reserix.api.chat.enums.ChatToolName;
import com.reserix.api.chat.enums.ChatToolResultStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ChatToolExecutor {

    private final Map<ChatToolName, ChatTool> toolsByName = new EnumMap<>(ChatToolName.class);

    public ChatToolExecutor(List<ChatTool> tools) {
        for (ChatTool tool : tools) {
            toolsByName.put(tool.name(), tool);
        }
    }

    public Optional<ChatToolResult> executeForIntent(ChatIntent intent, ChatToolContext context) {
        return resolveReadOnlyTool(intent).map(toolName -> execute(toolName, buildDefaultRequest(toolName, context), context));
    }

    public ChatToolResult execute(ChatToolName toolName, ChatToolRequest request, ChatToolContext context) {
        ChatTool tool = toolsByName.get(toolName);
        if (tool == null) {
            return new ChatToolResult(
                    toolName,
                    ChatToolResultStatus.ERROR,
                    "This chatbot capability is not available yet.",
                    null,
                    "No tool registered for " + toolName);
        }
        try {
            return tool.execute(request, context);
        } catch (Exception exception) {
            return ChatToolResult.error(
                    toolName,
                    "Something went wrong while processing this request. Please try again.",
                    exception);
        }
    }

    private Optional<ChatToolName> resolveReadOnlyTool(ChatIntent intent) {
        if (intent == null) return Optional.empty();
        return switch (intent) {
            case MOVIE_SEARCH -> Optional.of(ChatToolName.SEARCH_MOVIES);
            case SCREENING_SEARCH -> Optional.of(ChatToolName.SEARCH_SCREENINGS);
            case SEAT_STATUS_CHECK -> Optional.of(ChatToolName.GET_SEAT_STATUS);
            case RESERVATION_LOOKUP -> Optional.of(ChatToolName.GET_MY_RESERVATIONS);
            case PAYMENT_STATUS_CHECK -> Optional.of(ChatToolName.GET_PAYMENT_STATUS);
            case FAQ -> Optional.of(ChatToolName.SEARCH_FAQ);
            default -> Optional.empty();
        };
    }

    private ChatToolRequest buildDefaultRequest(ChatToolName toolName, ChatToolContext context) {
        String message = context.rawUserMessage() == null ? "" : context.rawUserMessage();
        Map<String, Object> args = switch (toolName) {
            case SEARCH_MOVIES -> Map.of("keyword", message);
            case SEARCH_SCREENINGS -> Map.of("movieTitle", message);
            case GET_SEAT_STATUS -> Map.of("query", message);
            case GET_MY_RESERVATIONS -> Map.of();
            case GET_PAYMENT_STATUS -> Map.of("query", message);
            case SEARCH_FAQ -> Map.of("query", message);
            default -> Map.of("query", message);
        };
        return new ChatToolRequest(toolName, args);
    }
}
