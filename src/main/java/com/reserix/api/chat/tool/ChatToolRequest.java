package com.reserix.api.chat.tool;

import com.reserix.api.chat.enums.ChatToolName;

import java.util.Map;

public record ChatToolRequest(
        ChatToolName toolName,
        Map<String, Object> arguments
) {
    public String stringArg(String name) {
        Object value = arguments == null ? null : arguments.get(name);
        return value == null ? null : String.valueOf(value);
    }

    public Long longArg(String name) {
        Object value = arguments == null ? null : arguments.get(name);
        if (value == null) return null;
        if (value instanceof Number number) return number.longValue();
        try { return Long.valueOf(String.valueOf(value)); }
        catch (NumberFormatException ignored) { return null; }
    }
}
