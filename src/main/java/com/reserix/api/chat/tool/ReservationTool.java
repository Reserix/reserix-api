package com.reserix.api.chat.tool;

import com.reserix.api.chat.enums.ChatToolName;
import com.reserix.api.chat.tool.port.ReservationItem;
import com.reserix.api.chat.tool.port.ReservationLookupPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationTool implements ChatTool {
    private final ObjectProvider<ReservationLookupPort> reservationLookupPortProvider;

    public ReservationTool(ObjectProvider<ReservationLookupPort> reservationLookupPortProvider) {
        this.reservationLookupPortProvider = reservationLookupPortProvider;
    }

    @Override
    public ChatToolName name() {
        return ChatToolName.GET_MY_RESERVATIONS;
    }

    @Override
    public ChatToolResult execute(ChatToolRequest request, ChatToolContext context) {
        if (context.userId() == null)
            return ChatToolResult.authRequired(name());

        ReservationLookupPort port = reservationLookupPortProvider.getIfAvailable();
        if (port == null)
            return ChatToolResult.notWired(name(), "Reservation lookup is ready at the chatbot tool layer, but it is not connected to the real ReservationService yet.");

        List<ReservationItem> reservations = port.getMyReservations(context.userId());
        if (reservations == null || reservations.isEmpty())
            return ChatToolResult.empty(name(), "You do not have any reservations yet.");

        String summary = reservations.stream().limit(5).map(item -> "- " + item.movieTitle() + " / " + item.startTime() + " / seats: " + String.join(", ", item.seats()) + " / status: " + item.reservationStatus()).collect(Collectors.joining("\n"));
        return ChatToolResult.success(name(), "Your reservations:\n" + summary, reservations);
    }
}
