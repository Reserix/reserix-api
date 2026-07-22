package com.reserix.api.chat.tool;

import com.reserix.api.chat.enums.ChatToolName;
import com.reserix.api.chat.tool.port.SeatStatusCriteria;
import com.reserix.api.chat.tool.port.SeatStatusItem;
import com.reserix.api.chat.tool.port.SeatStatusPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SeatTool implements ChatTool {
    private final ObjectProvider<SeatStatusPort> seatStatusPortProvider;

    public SeatTool(ObjectProvider<SeatStatusPort> seatStatusPortProvider) {
        this.seatStatusPortProvider = seatStatusPortProvider;
    }

    @Override
    public ChatToolName name() {
        return ChatToolName.GET_SEAT_STATUS;
    }

    @Override
    public ChatToolResult execute(ChatToolRequest request, ChatToolContext context) {
        SeatStatusPort port = seatStatusPortProvider.getIfAvailable();
        if (port == null)
            return ChatToolResult.notWired(name(), "Seat status checking is ready at the chatbot tool layer, but it is not connected to the real SeatService yet.");

        Long screeningId = request.longArg("screeningId");
        if (screeningId == null)
            return ChatToolResult.validationFailed(name(), "Please choose a screening time first so I can check seat availability.");

        List<SeatStatusItem> seats = port.getSeatStatus(new SeatStatusCriteria(screeningId, List.of()));
        if (seats == null || seats.isEmpty())
            return ChatToolResult.empty(name(), "No seat information was found for that screening.");

        String summary = seats.stream().limit(10).map(seat -> "- " + seat.code() + ": " + seat.status()).collect(Collectors.joining("\n"));
        return ChatToolResult.success(name(), "Seat status:\n" + summary, seats);
    }
}
