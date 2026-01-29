package com.payment.payment_service.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiConstant {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class STRIPE {
        public static final String BASE = "stripe";
        public static final String PAYMENT_INTENT = BASE + "/payment-intent";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AUTH0 {
        public static final String AUTHORIZE_URL = "authorize";
        public static final String DEVICE_AUTHORIZATION_URL = "oauth/device/code";
        public static final String TOKEN_URL = "oauth/token";
        public static final String USER_INFO_URL = "userinfo";
        public static final String OPENID_CONFIG = ".well-known/openid-configuration";
        public static final String JWKS_JSON = ".well-known/jwks.json";
    }
}