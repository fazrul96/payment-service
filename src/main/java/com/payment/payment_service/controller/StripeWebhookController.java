package com.payment.payment_service.controller;

import com.payment.payment_service.config.swagger.DefaultApiResponses;
import com.payment.payment_service.constants.ApiConstant;
import com.payment.payment_service.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "${app.privateApiPath}")
@CrossOrigin(origins = "${app.basePath}")
@Tag(name = "Payment Stripe", description = "Endpoints for managing payment stripe webhook")
public class StripeWebhookController extends BaseController {
    private final StripeService stripeService;

    @Value("${stripe.secret-key}")
    private String webhookSecret;

    @Override
    protected String getControllerName() {
        return "StripeWebhookController";
    }

    @Operation(summary = "Initiate payment intent")
    @DefaultApiResponses
    @PostMapping(path = "/stripe/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature
    ) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        stripeService.processWebhookEvent(event);
        return ResponseEntity.ok().build();
    }
}