package com.payment.payment_service.component;

import com.payment.payment_service.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

import static com.payment.payment_service.constants.GeneralConstant.DOUBLE_ASTERISKS;

@Component
@RequiredArgsConstructor
public class AuthorizationPolicy {
    private final AppProperties appProperties;

    public void configureAuthorization(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
                .requestMatchers(excludeMatchers()).permitAll()
                .requestMatchers(publicMatchers()).permitAll()
                .requestMatchers(privateMatchers()).authenticated()
                .anyRequest().authenticated();
    }

    private String[] publicMatchers() {
        return new String[] {
                appProperties.getPublicApiPath() + DOUBLE_ASTERISKS,
                "/actuator/health"
        };
    }

    private String[] privateMatchers() {
        return new String[] {
                appProperties.getPrivateApiPath() + DOUBLE_ASTERISKS
        };
    }

    private String[] excludeMatchers() {
        return new String[] {
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/api-docs/**",
                "/swagger-resources/**"
        };
    }
}