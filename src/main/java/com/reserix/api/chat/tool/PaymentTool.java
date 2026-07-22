package com.reserix.api.chat.tool;

import com.reserix.api.chat.enums.ChatToolName;
import com.reserix.api.chat.tool.port.PaymentStatusInfo;
import com.reserix.api.chat.tool.port.PaymentStatusPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class PaymentTool implements ChatTool {
    private final ObjectProvider<PaymentStatusPort> paymentStatusPortProvider;

    public PaymentTool(ObjectProvider<PaymentStatusPort> paymentStatusPortProvider) {
        this.paymentStatusPortProvider = paymentStatusPortProvider;
    }

    @Override
    public ChatToolName name() {
        return ChatToolName.GET_PAYMENT_STATUS;
    }

    @Override
    public ChatToolResult execute(ChatToolRequest request, ChatToolContext context) {
        if (context.userId() == null)
            return ChatToolResult.authRequired(name());

        PaymentStatusPort port = paymentStatusPortProvider.getIfAvailable();
        if (port == null)
            return ChatToolResult.notWired(name(), "Payment status checking is ready at the chatbot tool layer, but it is not connected to the real PaymentService yet.");

        Long reservationId = request.longArg("reservationId");
        if (reservationId == null)
            return ChatToolResult.validationFailed(name(), "Please choose a reservation first so I can check payment status.");

        PaymentStatusInfo paymentStatus = port.getPaymentStatus(context.userId(), reservationId);
        if (paymentStatus == null)
            return ChatToolResult.empty(name(), "No payment information was found for that reservation.");

        String summary = "Reservation " + paymentStatus.reservationId() + " is " + paymentStatus.reservationStatus() + ", payment status is " + paymentStatus.paymentStatus() + ".";
        return ChatToolResult.success(name(), summary, paymentStatus);
    }
}
