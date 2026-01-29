package com.payment.payment_service.config;

import com.payment.payment_service.properties.AuthProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import static com.payment.payment_service.constants.ApiConstant.AUTH0.JWKS_JSON;

@Configuration
public class JwtDecoderConfig {

    @Bean
    public JwtDecoder jwtDecoder(AuthProperties authProperties) {
        return NimbusJwtDecoder.withJwkSetUri(
                authProperties.getIssuer() + JWKS_JSON
        ).build();
    }
}