package com.payment.payment_service.controller;

import com.payment.payment_service.config.swagger.DefaultApiResponses;
import com.payment.payment_service.constants.ApiConstant;
import com.payment.payment_service.dto.RequestContext;
import com.payment.payment_service.dto.request.StripeRequest;
import com.payment.payment_service.dto.response.ApiResponseDto;
import com.payment.payment_service.dto.response.StripeResponse;
import com.payment.payment_service.service.StripeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "${app.privateApiPath}")
@CrossOrigin(origins = "${app.basePath}")
@Tag(name = "Payment Stripe", description = "Endpoints for managing payment stripe")
public class StripeController extends BaseController {
    private final StripeService stripeService;

    @Override
    protected String getControllerName() {
        return "StripeController";
    }

    @Operation(summary = "Initiate payment intent")
    @DefaultApiResponses
    @PostMapping(path = ApiConstant.STRIPE.PAYMENT_INTENT)
    public ApiResponseDto<StripeResponse> createPaymentIntent(
        RequestContext context,
        @Valid @RequestBody StripeRequest request
    ) {
        return handleRequest(getControllerName() + "createPaymentIntent",
                context, () -> stripeService.createPaymentIntent(
                        context.getRequestId(), context.getUserId(), request)
        );
    }

    @Operation(summary = "Retrieve payment intent")
    @DefaultApiResponses
    @GetMapping(path = ApiConstant.STRIPE.PAYMENT_INTENT + "/{id}")
    public ApiResponseDto<StripeResponse> retrievePaymentIntent(
            RequestContext context,
            @PathVariable String id
    ) {
        return handleRequest(
                getControllerName() + "retrievePaymentIntent",
                context, () -> stripeService.retrievePaymentIntent(
                        context.getRequestId(), id)
        );
    }

    @Operation(summary = "Refund payment")
    @DefaultApiResponses
    @PostMapping(path = ApiConstant.STRIPE.BASE +"/refund/{paymentIntentId}")
    public ApiResponseDto<Void> refundPayment(
            RequestContext context,
            @PathVariable String paymentIntentId
    ) {
        return handleRequest(
                getControllerName() + "refundPayment", context, () -> {
                    stripeService.refundPayment(context.getRequestId(), paymentIntentId);
                    return null;
                }
        );
    }
}