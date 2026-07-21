package com.reserix.api.chat.service;

import com.reserix.api.chat.enums.ChatIntent;
import org.springframework.stereotype.Component;

@Component
public class ChatDraftResponder {

    /*
     * Temporary deterministic responses.
     *
     * This class exists only so the API can be tested before LLM/tool integration.
     * Replace these responses later with LLM + backend tool results.
     */
    public String createResponse(ChatIntent intent, String userMessage) {
        return switch (intent) {
            case GREETING -> "Hello, I am Reserix AI Assistant. I can help with movie search, screening times, seat availability, reservation lookup, and payment status checks.";
            case MOVIE_SEARCH -> "I can help you search for movies. The next implementation step is to connect this request to MovieTool.searchMovies().";
            case SCREENING_SEARCH -> "Which movie’s screening times would you like to check?";
            case SEAT_STATUS_CHECK -> "I can check seat availability after the selected screening is known. The next implementation step is to connect this request to SeatTool.getSeatStatus().";
            case RESERVATION_HELP -> "I can help you make a reservation, but I must check the screening and seat availability first. I will ask for confirmation before creating any pending reservation.";
            case RESERVATION_LOOKUP -> "I can show your reservations after this API is connected to ReservationTool.getMyReservations().";
            case RESERVATION_CANCEL_REQUEST -> "I can help cancel a reservation, but I will ask for explicit confirmation before cancellation.";
            case PAYMENT_STATUS_CHECK -> "I can check payment status after this API is connected to PaymentTool.getPaymentStatus().";
            case FAQ -> "I can answer reservation and payment policy questions after this API is connected to FaqTool.searchFaq().";
            case UNKNOWN -> "Sorry, I can’t handle that request yet. I can help with movie search, screening times, seat availability, reservation lookup, and payment status checks.";
        };
    }
}
