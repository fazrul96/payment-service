package com.payment.payment_service.exception;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

@SuppressWarnings({"PMD.PackageCase"})
public interface ErrorType extends Serializable {
    String getCode();

    String getDesc();

    HttpStatus getHttpStatusCode();
}