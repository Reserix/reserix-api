package com.reserix.api.chat.service;

import com.reserix.api.chat.enums.ChatIntent;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ChatIntentRouter {

    public ChatIntent detectIntent(String rawMessage) {
        if (rawMessage == null || rawMessage.isBlank()) {
            return ChatIntent.UNKNOWN;
        }

        String message = rawMessage.toLowerCase(Locale.ROOT).trim();

        if (containsAny(message, "Good morning", "hello", "hi", "evening", "what can you do")) {
            return ChatIntent.GREETING;
        }

        if (containsAny(message, "exit", "cancel")) {
            return ChatIntent.RESERVATION_CANCEL_REQUEST;
        }

        if (containsAny(message, "my reservation", "reservation history", "show me reservation", "reservation", "my booking", "my ticket")) {
            return ChatIntent.RESERVATION_LOOKUP;
        }

        if (containsAny(message, "bill", "payment", "paid", "pay")) {
            return ChatIntent.PAYMENT_STATUS_CHECK;
        }

        if (containsAny(message, "help", "book", "seat", "a1", "a2", "a3", "a4")) {
            if (containsAny(message, "book", "lock", "take", "book", "reserve")) {
                return ChatIntent.RESERVATION_HELP;
            }
            return ChatIntent.SEAT_STATUS_CHECK;
        }

        if (containsAny(message, "time", "screening", "showtime", "time")) {
            return ChatIntent.SCREENING_SEARCH;
        }

        if (containsAny(message, "short", "movie", "film", "recommend")) {
            return ChatIntent.MOVIE_SEARCH;
        }

        if (containsAny(message, "refund", "faq", "help", "how long")) {
            return ChatIntent.FAQ;
        }

        return ChatIntent.UNKNOWN;
    }

    private boolean containsAny(String message, String... needles) {
        for (String needle : needles) {
            if (message.contains(needle)) {
                return true;
            }
        }
        return false;
    }
}
