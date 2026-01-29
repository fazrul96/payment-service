package com.payment.payment_service.service.impl;

import com.payment.payment_service.dto.request.StripeRequest;
import com.payment.payment_service.dto.response.StripeResponse;
import com.payment.payment_service.exception.WebException;
import com.payment.payment_service.service.StripeService;
import com.payment.payment_service.util.LogUtils;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeServiceImpl implements StripeService {
    private static final String SOURCE = "insurance-portal";
    private static final String DEFAULT_CURRENCY = "MYR";

    private final LogUtils logUtils;

    @Override
    public String getServiceName() {
        return "StripeServiceImpl";
    }

    @Override
    public StripeResponse createPaymentIntent(String requestId, String userId, StripeRequest request) {
        logUtils.logRequest(requestId, getServiceName() + "createPaymentIntent");

        validateRequest(request);

        String currency = Optional.ofNullable(request.getCurrency())
                .filter(c -> !c.isBlank())
                .map(String::toUpperCase)
                .orElse(DEFAULT_CURRENCY);

        long amountInSen = convertToMinorUnit(request.getAmount(), currency);

        PaymentIntentCreateParams params = buildPaymentIntentParams(request, userId, amountInSen, currency);
        RequestOptions options = buildRequestOptions(request);

        try {
            PaymentIntent intent = PaymentIntent.create(params, options);
            return mapToResponse(intent);
        } catch (StripeException ex) {
            throw new WebException("Failed to create Stripe payment intent", ex);
        }
    }

    @Override
    public StripeResponse retrievePaymentIntent(String requestId, String paymentIntentId) {
        logUtils.logRequest(requestId, getServiceName() + "retrievePaymentIntent");

        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            return mapToResponse(intent);
        } catch (StripeException ex) {
            throw new WebException("Failed to retrieve Stripe payment intent", ex);
        }
    }

    @Override
    public void refundPayment(String requestId, String paymentIntentId) {
        logUtils.logRequest(requestId, getServiceName() + "refundPayment");

        try {
            Refund.create(
                    RefundCreateParams.builder()
                            .setPaymentIntent(paymentIntentId)
                            .build()
            );
        } catch (StripeException ex) {
            throw new WebException("Refund failed", ex);
        }
    }

    private void validateRequest(StripeRequest request) {
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }

        if (request.getCurrency() == null || request.getCurrency().isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }

        if (request.getQuotationId() == null || request.getQuotationId().isBlank()) {
            throw new IllegalArgumentException("Quotation ID is required");
        }
    }

    private long convertToMinorUnit(long amount, String currency) {
        // Extendable for JPY, KRW, etc.
        return amount * 100;
    }

    private PaymentIntentCreateParams buildPaymentIntentParams(
            StripeRequest request, String userId, long amountInSen, String currency
    ) {
        return PaymentIntentCreateParams.builder()
                .setAmount(amountInSen)
                .setCurrency(currency)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .putMetadata("quotationId", request.getQuotationId())
                .putMetadata("userId", userId)
                .putMetadata("source", SOURCE)
                .build();
    }

    private RequestOptions buildRequestOptions(StripeRequest request) {
        return RequestOptions.builder()
                .setIdempotencyKey(request.getQuotationId() + "-create-intent")
                .build();
    }

    private StripeResponse mapToResponse(PaymentIntent intent) {
        return StripeResponse.builder()
                .id(intent.getId())
                .secret(intent.getClientSecret())
                .paymentMethodTypes(intent.getPaymentMethodTypes())
                .build();
    }

    @Override
    public void processWebhookEvent(Event event) {

        switch (event.getType()) {

            case "payment_intent.succeeded" -> {
                PaymentIntent intent = extractPaymentIntent(event);
                handleSucceeded(intent);
            }

            case "payment_intent.payment_failed" -> {
                PaymentIntent intent = extractPaymentIntent(event);
                handleFailed(intent);
            }

            case "payment_intent.canceled" -> {
                PaymentIntent intent = extractPaymentIntent(event);
                handleCanceled(intent);
            }

            case "charge.refunded" -> {
                handleRefund(event);
            }

            default -> log.debug("Unhandled Stripe event: {}", event.getType());
        }
    }

    private PaymentIntent extractPaymentIntent(Event event) {
        return (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() ->
                        new IllegalStateException("Unable to deserialize Stripe event")
                );
    }

    private void handleSucceeded(PaymentIntent intent) {
        String quotationId = intent.getMetadata().get("quotationId");

        // 1. Mark quotation as PAID
        // 2. Persist transaction reference
        // 3. Trigger policy issuance
    }

    private void handleFailed(PaymentIntent intent) {
        // 1. Mark quotation as PAYMENT_FAILED
        // 2. Allow retry
    }

    private void handleCanceled(PaymentIntent intent) {
        // 1. Mark payment as CANCELED
    }

    private void handleRefund(Event event) {
        // 1. Mark policy as REFUNDED
        // 2. Audit refund reference
    }
}
