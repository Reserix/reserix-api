package com.reserix.api.chat.tool;

import com.reserix.api.chat.enums.ChatToolName;

public interface ChatTool {
    ChatToolName name();
    ChatToolResult execute(ChatToolRequest request, ChatToolContext context);
}
