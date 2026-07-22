package com.reserix.api.chat.tool;

import com.reserix.api.chat.enums.ChatToolName;
import com.reserix.api.chat.enums.ChatToolResultStatus;

public record ChatToolResult(
        ChatToolName toolName,
        ChatToolResultStatus status,
        String userSafeSummary,
        Object data,
        String errorMessage
) {
    public static ChatToolResult success(ChatToolName toolName, String userSafeSummary, Object data) {
        return new ChatToolResult(toolName, ChatToolResultStatus.SUCCESS, userSafeSummary, data, null);
    }

    public static ChatToolResult empty(ChatToolName toolName, String userSafeSummary) {
        return new ChatToolResult(toolName, ChatToolResultStatus.EMPTY, userSafeSummary, null, null);
    }

    public static ChatToolResult authRequired(ChatToolName toolName) {
        return new ChatToolResult(toolName, ChatToolResultStatus.AUTH_REQUIRED, "Please sign in first.", null, "Authentication required");
    }

    public static ChatToolResult validationFailed(ChatToolName toolName, String message) {
        return new ChatToolResult(toolName, ChatToolResultStatus.VALIDATION_FAILED, message, null, message);
    }

    public static ChatToolResult notWired(ChatToolName toolName, String nextStep) {
        return new ChatToolResult(toolName, ChatToolResultStatus.ERROR, nextStep, null, "Tool port is not connected to the real Reserix service yet.");
    }

    public static ChatToolResult error(ChatToolName toolName, String userSafeSummary, Exception exception) {
        return new ChatToolResult(toolName, ChatToolResultStatus.ERROR, userSafeSummary, null, exception == null ? null : exception.getMessage());
    }
}
