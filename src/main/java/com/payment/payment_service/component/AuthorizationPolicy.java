package com.payment.payment_service.component;

import com.payment.payment_service.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

import static com.payment.payment_service.constants.GeneralConstant.DOUBLE_ASTERISKS;
import static com.payment.payment_service.constants.SecurityConstant.ADDITIONAL_PATHS;

@Component
@RequiredArgsConstructor
public class AuthorizationPolicy {
    private final AppProperties appProperties;

    public void configureAuthorization(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
                .requestMatchers(ADDITIONAL_PATHS).permitAll()
                .requestMatchers(appProperties.getPublicApiPath() + DOUBLE_ASTERISKS).permitAll()
                .requestMatchers(appProperties.getPrivateApiPath() + DOUBLE_ASTERISKS).authenticated()
                .anyRequest().authenticated();
    }
}