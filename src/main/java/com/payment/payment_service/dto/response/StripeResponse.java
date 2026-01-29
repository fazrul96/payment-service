package com.payment.payment_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StripeResponse {
    private String id;
    private String secret;
    private List<String> paymentMethodTypes;
}
