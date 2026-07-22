package com.reserix.api.chat.tool;

import com.reserix.api.chat.enums.ChatToolName;
import com.reserix.api.chat.tool.port.ScreeningSearchCriteria;
import com.reserix.api.chat.tool.port.ScreeningSearchItem;
import com.reserix.api.chat.tool.port.ScreeningSearchPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScreeningTool implements ChatTool {
    private final ObjectProvider<ScreeningSearchPort> screeningSearchPortProvider;

    public ScreeningTool(ObjectProvider<ScreeningSearchPort> screeningSearchPortProvider) {
        this.screeningSearchPortProvider = screeningSearchPortProvider;
    }

    @Override
    public ChatToolName name() {
        return ChatToolName.SEARCH_SCREENINGS;
    }

    @Override
    public ChatToolResult execute(ChatToolRequest request, ChatToolContext context) {
        ScreeningSearchPort port = screeningSearchPortProvider.getIfAvailable();
        if (port == null)
            return ChatToolResult.notWired(name(), "Screening search is ready at the chatbot tool layer, but it is not connected to the real ScreeningService yet.");

        List<ScreeningSearchItem> screenings = port.searchScreenings(new ScreeningSearchCriteria(
                null,
                request.stringArg("movieTitle"),
                null,
                null,
                null));
        if (screenings == null || screenings.isEmpty())
            return ChatToolResult.empty(name(), "No matching screening times were found.");

        String summary = screenings.stream().limit(5).map(item ->
                "- " + item.movieTitle() + " / " + item.startTime() + " / " + safe(item.theaterName()) + " " + safe(item.roomName()) + " / available seats: " + safe(item.availableSeatCount())).collect(Collectors.joining("\n"));
        return ChatToolResult.success(name(), "I found these screening times:\n" + summary, screenings);
    }
    private String safe(Object value) { return value == null ? "-" : String.valueOf(value); }
}
