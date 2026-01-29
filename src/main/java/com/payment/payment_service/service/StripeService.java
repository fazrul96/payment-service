package com.payment.payment_service.service;

import com.payment.payment_service.dto.request.StripeRequest;
import com.payment.payment_service.dto.response.StripeResponse;
import com.stripe.model.Event;

public interface StripeService {
    StripeResponse createPaymentIntent(String requestId, String userId, StripeRequest request);

    StripeResponse retrievePaymentIntent(String requestId, String paymentIntentId);

    void refundPayment(String requestId, String paymentIntentId);

    void processWebhookEvent(Event event);

    String getServiceName();
}
