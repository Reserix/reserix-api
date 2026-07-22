package com.reserix.api.chat.tool;

import com.reserix.api.chat.enums.ChatToolName;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class FaqTool implements ChatTool {
    private final List<FaqItem> faqItems = List.of(
            new FaqItem(List.of("payment", "pay", "What time", "how long"), "You must complete payment within the configured reservation hold time. In the MVP policy, this is expected to be 5 minutes unless the backend configuration says otherwise."),
            new FaqItem(List.of("refund", "get back"), "Refund handling depends on the reservation and payment status. The chatbot should only explain the policy; actual refund processing must be handled by the payment/refund service."),
            new FaqItem(List.of("cancel", "close"), "A reservation can be cancelled only if it belongs to the signed-in user and is still in a cancellable status."),
            new FaqItem(List.of("seat"), "Seat availability must be checked in real time. A seat can become unavailable if another user reserves it first.")
    );

    @Override
    public ChatToolName name() {
        return ChatToolName.SEARCH_FAQ;
    }

    @Override
    public ChatToolResult execute(ChatToolRequest request, ChatToolContext context) {
        String query = request.stringArg("query");
        if (query == null || query.isBlank())
            return ChatToolResult.validationFailed(name(), "Please enter a question.");

        String normalized = query.toLowerCase(Locale.ROOT);
        for (FaqItem item : faqItems) {
            for (String keyword : item.keywords()) {
                if (normalized.contains(keyword.toLowerCase(Locale.ROOT)))
                    return ChatToolResult.success(name(), item.answer(), item);
            }
        }
        return ChatToolResult.empty(
                name(),
                "I could not find an approved FAQ answer for that. I can help with movie search, screening times, seat availability, reservation lookup, and payment status checks.");
    }

    private record FaqItem(List<String> keywords, String answer) { }
}
