package com.reserix.api.payment.controller;

import com.reserix.api.common.response.ApiResponse;
import com.reserix.api.payment.dto.MockPaymentIntentRequest;
import com.reserix.api.payment.dto.MockPaymentIntentResponse;
import com.reserix.api.payment.dto.MockPaymentWebhookRequest;
import com.reserix.api.payment.service.MockPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments/mock")
@RequiredArgsConstructor
public class MockPaymentController {
    private final MockPaymentService mockPaymentService;

    @PostMapping("/intent")
    public ResponseEntity<ApiResponse<MockPaymentIntentResponse>> createIntent(
            @RequestBody MockPaymentIntentRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Mock payment intent created", mockPaymentService.createIntent(request)));
    }

    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<Void>> webhook(
            @RequestBody MockPaymentWebhookRequest request
    ) {
        mockPaymentService.handleWebhook(request);
        return ResponseEntity.ok().build();
    }
}
